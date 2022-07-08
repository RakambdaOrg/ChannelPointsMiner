package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.EventCreated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.EventUpdated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PredictionMade;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PredictionResult;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.PredictionUpdated;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Prediction;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.event.impl.EventCreatedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.EventUpdatedEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionMadeEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.PredictionResultEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.raksrinana.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.miner.handler.data.PredictionState;
import fr.raksrinana.channelpointsminer.miner.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.prediction.bet.BetPlacer;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor
@Log4j2
public class PredictionsHandler extends HandlerAdapter{
	private static final int OFFSET = 5;
	
	private final IMiner miner;
	private final BetPlacer betPlacer;
    
    private final boolean recordPlacedPredictions;
	
	@Getter(value = PROTECTED, onMethod_ = {
			@TestOnly,
			@VisibleForTesting
	})
	private final Map<String, BettingPrediction> predictions = new ConcurrentHashMap<>();
	@Getter(value = PROTECTED, onMethod_ = {
			@TestOnly,
			@VisibleForTesting
	})
	private final Map<String, PlacedPrediction> placedPredictions = new ConcurrentHashMap<>();
	
	@Override
	public void onEventCreated(@NotNull Topic topic, @NotNull EventCreated message){
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
	public void onEventUpdated(@NotNull Topic topic, @NotNull EventUpdated message){
		var streamer = miner.getStreamerById(topic.getTarget()).orElse(null);
		var event = message.getData().getEvent();
		try(var ignored = LogContext.with(miner).withStreamer(streamer).withEventId(event.getId())){
            var prediction = predictions.get(event.getId());
            
            if(Objects.isNull(streamer)){
                log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
                return;
            }
            
            if(recordPlacedPredictions){
                log.debug("Sending an event update event.");
                miner.onEvent(new EventUpdatedEvent(miner, streamer, event));
            }
            
			if(Objects.isNull(prediction)){
				log.debug("Event update on unknown prediction, creating it");
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
	public void onPredictionMade(@NotNull Topic topic, @NotNull PredictionMade message){
		predictionPlaced(message.getData().getPrediction());
	}
	
	@Override
	public void onPredictionResult(@NotNull Topic topic, @NotNull PredictionResult message){
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
			miner.onEvent(new PredictionResultEvent(miner, streamerId, username, streamer, placedPredictions.get(eventId), message.getData()));
			predictions.remove(eventId);
			placedPredictions.remove(eventId);
		}
	}
	
	@Override
	public void onPredictionUpdated(@NotNull Topic topic, @NotNull PredictionUpdated message){
		predictionPlaced(message.getData().getPrediction());
	}
	
	private void predictionPlaced(@NotNull Prediction predictionData){
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
			miner.onEvent(new PredictionMadeEvent(miner, streamerId, username, streamer, placedPrediction));
		}
	}
	
	private void onNewPrediction(@NotNull Streamer streamer, @NotNull Event event){
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
		
		miner.onEvent(new EventCreatedEvent(miner, streamer, event));
		prediction.setState(PredictionState.SCHEDULING);
		schedulePrediction(streamer, prediction);
	}
	
	private boolean hasEnoughPoints(@NotNull Streamer streamer){
		var requiredPoints = streamer.getSettings().getPredictions().getMinimumPointsRequired();
		return streamer.getChannelPoints().map(points -> points >= requiredPoints).orElse(false);
	}
	
	@NotNull
	private BettingPrediction createPrediction(@NotNull Streamer streamer, @NotNull Event event){
		return BettingPrediction.builder()
				.streamer(streamer)
				.event(event)
				.lastUpdate(event.getCreatedAt())
				.build();
	}
	
	private void schedulePrediction(@NotNull Streamer streamer, @NotNull BettingPrediction bettingPrediction){
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
