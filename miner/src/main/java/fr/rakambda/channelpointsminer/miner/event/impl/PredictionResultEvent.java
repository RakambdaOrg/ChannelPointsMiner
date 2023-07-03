package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.ws.data.message.predictionresult.PredictionResultData;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PredictionResultPayload;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.PredictionResultType;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.event.EventVariableKey;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PredictionResultEvent extends AbstractLoggableStreamerEvent{
	private final PlacedPrediction placedPrediction;
	@Getter
	private final PredictionResultData predictionResultData;
	
	public PredictionResultEvent(@NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @Nullable PlacedPrediction placedPrediction, @NotNull PredictionResultData predictionResultData){
		super(streamerId, streamerUsername, streamer, predictionResultData.getTimestamp().toInstant());
		this.placedPrediction = placedPrediction;
		this.predictionResultData = predictionResultData;
	}
	
	@Override
	@NotNull
	public String getConsoleLogFormat(){
		return "Bet result [{prediction_type} | {prediction_points}]";
	}
	
	@Override
	@NotNull
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
	@NotNull
	public Map<String, String> getEmbedFields(){
		return Map.of(
				"Type", EventVariableKey.PREDICTION_TYPE,
				"Points gained", EventVariableKey.PREDICTION_POINTS
		);
	}
	
	@Override
	@NotNull
	protected String getColor(){
		return COLOR_PREDICTION;
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "🧧";
	}
	
	@NotNull
	private PredictionResultType getType(){
		var result = Optional.ofNullable(predictionResultData.getPrediction().getResult());
		return result.map(PredictionResultPayload::getType).orElse(PredictionResultType.UNKNOWN);
	}
	
	@NotNull
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
