package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.EventCreated;
import fr.raksrinana.channelpointsminer.api.ws.data.message.EventUpdated;
import fr.raksrinana.channelpointsminer.api.ws.data.message.PredictionMade;
import fr.raksrinana.channelpointsminer.api.ws.data.message.PredictionResult;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.EventStatus;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import fr.raksrinana.channelpointsminer.handler.data.PredictionState;
import fr.raksrinana.channelpointsminer.log.LogContext;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Log4j2
public class PredictionsHandler extends HandlerAdapter{
	private static final int OFFSET = 5;
	
	private final IMiner miner;
	private final BetPlacer betPlacer;
	
	private final Map<String, Prediction> predictions = new ConcurrentHashMap<>();
	
	@Override
	public void onEventCreated(@NotNull Topic topic, @NotNull EventCreated message){
		var event = message.getData().getEvent();
		try(var ignored = LogContext.empty().withEventId(event.getId())){
			if(event.getStatus() != EventStatus.ACTIVE){
				log.debug("Event is not active");
				return;
			}
			
			var streamerOptional = miner.getStreamerById(topic.getTarget());
			if(streamerOptional.isEmpty()){
				log.warn("Couldn't find associated streamer with target {}", topic.getTarget());
				return;
			}
			var streamer = streamerOptional.get();
			
			if(!hasEnoughPoints(streamer)){
				log.info("Not placing bet, not enough points");
				return;
			}
			
			var prediction = predictions.computeIfAbsent(event.getId(), key -> createPrediction(streamer, event));
			if(prediction.getState() != PredictionState.CREATED){
				log.debug("Prediction has already been placed");
				return;
			}
			
			prediction.setState(PredictionState.SCHEDULING);
			schedulePrediction(streamer, prediction);
		}
	}
	
	@Override
	public void onEventUpdated(@NotNull Topic topic, @NotNull EventUpdated message){
		var event = message.getData().getEvent();
		try(var ignored = LogContext.empty().withEventId(event.getId())){
			var prediction = predictions.get(event.getId());
			if(Objects.isNull(prediction)){
				log.debug("Event update on unknown prediction");
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
		var eventId = message.getData().getPrediction().getEventId();
		try(var ignored = LogContext.empty().withEventId(eventId)){
			log.info("Bet placed successfully");
			Optional.ofNullable(predictions.get(eventId))
					.ifPresentOrElse(
							prediction -> prediction.setState(PredictionState.PLACED),
							() -> log.warn("Bet was placed on an unknown prediction. Was it made manually?")
					);
		}
	}
	
	@Override
	public void onPredictionResult(@NotNull Topic topic, @NotNull PredictionResult message){
		super.onPredictionResult(topic, message); //TODO
	}
	
	private boolean hasEnoughPoints(@NotNull Streamer streamer){
		var requiredPoints = streamer.getSettings().getPredictions().getMinimumPointsRequired();
		return streamer.getChannelPoints().map(points -> points >= requiredPoints).orElse(false);
	}
	
	@NotNull
	private Prediction createPrediction(@NotNull Streamer streamer, @NotNull Event event){
		return Prediction.builder()
				.streamer(streamer)
				.event(event)
				.lastUpdate(event.getCreatedAt())
				.build();
	}
	
	private void schedulePrediction(@NotNull Streamer streamer, @NotNull Prediction prediction){
		var delayCalculator = streamer.getSettings().getPredictions().getDelayCalculator();
		var event = prediction.getEvent();
		
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
		miner.schedule(() -> betPlacer.placeBet(prediction), secondsDelay, TimeUnit.SECONDS);
		prediction.setState(PredictionState.SCHEDULED);
	}
}
