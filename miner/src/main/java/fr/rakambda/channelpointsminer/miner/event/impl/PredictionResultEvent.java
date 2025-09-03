package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultPayload;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.PredictionResultType;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PredictionResultEvent extends AbstractLoggableStreamerEvent{
	private final PlacedPrediction placedPrediction;
	@Getter
	private final PredictionResultData predictionResultData;
	
	public PredictionResultEvent(@NonNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @Nullable PlacedPrediction placedPrediction, @NonNull PredictionResultData predictionResultData){
		super(streamerId, streamerUsername, streamer, predictionResultData.getTimestamp().toInstant());
		this.placedPrediction = placedPrediction;
		this.predictionResultData = predictionResultData;
	}
	
	@Override
	@NonNull
	public String getConsoleLogFormat(){
		return "Bet result [{prediction_type} | {prediction_points}]";
	}
	
	@Override
	@NonNull
	public String getDefaultFormat(){
		return "[{username}] {emoji} {streamer} : Bet result [{prediction_type} | {prediction_points}]";
	}
	
	@Override
	public String lookup(String key){
		if(EventVariableKey.PREDICTION_TYPE.equals(key)){
			return getType().toString();
		}
		if(EventVariableKey.PREDICTION_POINTS.equals(key)){
			return getGain();
		}
		return super.lookup(key);
	}
	
	@Override
	@NonNull
	public Map<String, String> getEmbedFields(){
		return Map.of(
				"Type", EventVariableKey.PREDICTION_TYPE,
				"Points gained", EventVariableKey.PREDICTION_POINTS
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
		return "🧧";
	}
	
	@NonNull
	private PredictionResultType getType(){
		var result = Optional.ofNullable(predictionResultData.getPrediction().getResult());
		return result.map(PredictionResultPayload::getType).orElse(PredictionResultType.UNKNOWN);
	}
	
	@NonNull
	public String getGain(){
		if(getType() == PredictionResultType.REFUND){
			return "0";
		}
		
		var result = Optional.ofNullable(predictionResultData.getPrediction().getResult());
		int pointsWon = result.map(PredictionResultPayload::getPointsWon).orElse(0);
		
		return Optional.ofNullable(placedPrediction)
				.map(prediction -> pointsWon - prediction.getAmount())
				.map(value -> millify(value, true))
				.orElse("Unknown final gain, obtained %s points".formatted(millify(pointsWon, true)));
	}
}
