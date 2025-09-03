package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PredictionMadeEvent extends AbstractLoggableStreamerEvent{
	private static final String UNKNOWN_OUTCOME = "UnknownOutcome";
	
	@Getter
	private final PlacedPrediction placedPrediction;
	
	public PredictionMadeEvent(@NonNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NonNull PlacedPrediction placedPrediction){
		super(streamerId, streamerUsername, streamer, placedPrediction.getPredictedAt());
		this.placedPrediction = placedPrediction;
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Bet placed [{prediction_points} | {prediction_outcome}]";
	}
	
	@Override
	@NonNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Bet placed [{prediction_points} | {prediction_outcome}]";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.PREDICTION_POINTS.equals(key)){
			return millify(placedPrediction.getAmount(), false);
		}
		if(EventVariableKey.PREDICTION_OUTCOME.equals(key)){
			return getOutcome();
		}
		return super.lookup(key);
	}
	
	@Override
	@NonNull
	public Map<String, String> getEmbedFields(){
		return Map.of(
				"Points placed", EventVariableKey.PREDICTION_POINTS,
				"Outcome", EventVariableKey.PREDICTION_OUTCOME
		);
	}
	
	@Override
	@NonNull
	protected String getColor(){
		return COLOR_PREDICTION;
	}
	
	@Override
	@NonNull
	protected String getEmoji(){
		return "🪙";
	}
	
	@NonNull
	private String getOutcome(){
		return Optional.ofNullable(placedPrediction.getBettingPrediction())
				.map(BettingPrediction::getEvent)
				.map(Event::getOutcomes).stream()
				.flatMap(Collection::stream)
				.filter(outcome -> Objects.equals(outcome.getId(), placedPrediction.getOutcomeId()))
				.findFirst()
				.map(outcome -> outcome.getColor() + ": " + outcome.getTitle())
				.orElse(UNKNOWN_OUTCOME);
	}
}
