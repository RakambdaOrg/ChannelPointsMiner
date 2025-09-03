package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.EventCreated;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.EventUpdated;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionMade;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionResult;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.PredictionUpdated;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.EventStatus;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Prediction;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PredictionState;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.prediction.bet.BetPlacer;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor
@Log4j2
public class PredictionsHandler extends PubSubMessageHandlerAdapter{
	private static final int OFFSET = 5;
	
	private final IMiner miner;
	private final BetPlacer betPlacer;
	private final IEventManager eventManager;
	
	@Getter(value = PROTECTED)
	private final Map<String, BettingPrediction> predictions = new ConcurrentHashMap<>();
	@Getter(value = PROTECTED)
	private final Map<String, PlacedPrediction> placedPredictions = new ConcurrentHashMap<>();
	
	@Override
	public void onEventCreated(@NonNull Topic topic, @NonNull EventCreated message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		var event = message.getData().getEvent();
		try(var ignored = LogContext.with(miner).withStreamer(streamer).withEventId(event.getId())){
			if(Objects.isNull(streamer)){
				log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
				return;
			}
			
			onNewPrediction(streamer, event);
		}
	}
	
	@Override
	public void onEventUpdated(@NonNull Topic topic, @NonNull EventUpdated message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		var event = message.getData().getEvent();
		try(var ignored = LogContext.with(miner).withStreamer(streamer).withEventId(event.getId())){
            
            if(Objects.nonNull(streamer)){
	            eventManager.onEvent(new EventUpdatedEvent(TimeFactory.now(), streamer.getUsername(), event));
            }
            
            var prediction = predictions.get(event.getId());
			
			if(Objects.isNull(prediction)){
				log.debug("Event update on unknown prediction, creating it");
				
				if(Objects.isNull(streamer)){
					log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
					return;
				}
				
				onNewPrediction(streamer, event);
				return;
			}
			
			var eventDate = message.getData().getTimestamp();
			if(eventDate.isBefore(prediction.getLastUpdate())){
				log.debug("Event update from the past");
				return;
			}
			
			prediction.setLastUpdate(eventDate);
			prediction.setEvent(event);
		}
	}
	
	@Override
	public void onPredictionMade(@NonNull Topic topic, @NonNull PredictionMade message){
		predictionPlaced(message.getData().getPrediction());
	}
	
	@Override
	public void onPredictionResult(@NonNull Topic topic, @NonNull PredictionResult message){
		var predictionData = message.getData().getPrediction();
		var streamerId = predictionData.getChannelId();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		var eventId = predictionData.getEventId();
		try(var ignored = LogContext.with(miner).withStreamer(streamer).withEventId(eventId)){
			if(Objects.isNull(predictionData.getResult())){
				log.warn("Received prediction result without result data");
				return;
			}
			eventManager.onEvent(new PredictionResultEvent(streamerId, username, streamer, placedPredictions.get(eventId), message.getData()));
			predictions.remove(eventId);
			placedPredictions.remove(eventId);
		}
	}
	
	@Override
	public void onPredictionUpdated(@NonNull Topic topic, @NonNull PredictionUpdated message){
		predictionPlaced(message.getData().getPrediction());
	}
	
	private void predictionPlaced(@NonNull Prediction predictionData){
		var streamerId = predictionData.getChannelId();
		var streamer = miner.getStreamerById(streamerId).orElse(null);
		var username = Objects.isNull(streamer) ? null : streamer.getUsername();
		var eventId = predictionData.getEventId();
		try(var ignored = LogContext.with(miner).withStreamer(streamer).withEventId(eventId)){
			var placedPoints = predictionData.getPoints();
			
			var prediction = Optional.ofNullable(predictions.get(eventId))
					.map(p -> {
						p.setState(PredictionState.PLACED);
						return p;
					});
			
			var placedPrediction = PlacedPrediction.builder()
					.eventId(eventId)
					.bettingPrediction(prediction.orElse(null))
					.amount(placedPoints)
					.outcomeId(predictionData.getOutcomeId())
					.predictedAt(predictionData.getPredictedAt().toInstant())
					.build();
			
			placedPredictions.put(eventId, placedPrediction);
			eventManager.onEvent(new PredictionMadeEvent(streamerId, username, streamer, placedPrediction));
		}
	}
	
	private void onNewPrediction(@NonNull Streamer streamer, @NonNull Event event){
		if(event.getStatus() != EventStatus.ACTIVE){
			log.debug("Event is not active");
			return;
		}
		
		if(!hasEnoughPoints(streamer)){
			log.info("Not placing bet, not enough points");
			return;
		}
		
		var prediction = predictions.computeIfAbsent(event.getId(), key -> createPrediction(streamer, event));
		if(prediction.getState() != PredictionState.CREATED){
			log.debug("Event is already being handled");
			return;
		}
		
		eventManager.onEvent(new EventCreatedEvent(streamer, event));
		prediction.setState(PredictionState.SCHEDULING);
		schedulePrediction(streamer, prediction);
	}
	
	private boolean hasEnoughPoints(@NonNull Streamer streamer){
		var requiredPoints = streamer.getSettings().getPredictions().getMinimumPointsRequired();
		return streamer.getChannelPoints().map(points -> points >= requiredPoints).orElse(false);
	}
	
	@NonNull
	private BettingPrediction createPrediction(@NonNull Streamer streamer, @NonNull Event event){
		return BettingPrediction.builder()
				.streamer(streamer)
				.event(event)
				.lastUpdate(event.getCreatedAt())
				.build();
	}
	
	private void schedulePrediction(@NonNull Streamer streamer, @NonNull BettingPrediction bettingPrediction){
		var delayCalculator = streamer.getSettings().getPredictions().getDelayCalculator();
		var event = bettingPrediction.getEvent();
		
		var placeAt = delayCalculator.calculate(event);
		log.debug("Requested to place bet at {}", placeAt);
		
		var now = TimeFactory.nowZoned();
		var startOffset = event.getCreatedAt().plusSeconds(OFFSET);
		var endOffset = event.getCreatedAt().plusSeconds(event.getPredictionWindowSeconds()).minusSeconds(OFFSET);
		
		if(placeAt.isBefore(startOffset)){
			log.debug("Time is too early, forcing it to {}", startOffset);
			placeAt = startOffset;
		}
		else if(placeAt.isAfter(endOffset)){
			log.debug("Time is too late, forcing it to {}", endOffset);
			placeAt = endOffset;
		}
		
		var elapsed = event.getCreatedAt().until(now, ChronoUnit.SECONDS);
		var secondsDelay = Math.min(event.getPredictionWindowSeconds() - elapsed - OFFSET,
				Math.max(OFFSET, now.until(placeAt, ChronoUnit.SECONDS)));
		
		log.info("Will place bet after {} seconds", secondsDelay);
		miner.schedule(() -> betPlacer.placeBet(bettingPrediction), secondsDelay, TimeUnit.SECONDS);
		bettingPrediction.setState(PredictionState.SCHEDULED);
	}
}
