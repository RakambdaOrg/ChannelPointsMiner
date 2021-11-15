package fr.raksrinana.channelpointsminer.log.event;

import fr.raksrinana.channelpointsminer.api.discord.data.Field;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultType;
import fr.raksrinana.channelpointsminer.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PredictionResultLogEvent extends AbstractLogEvent{
	private final PlacedPrediction placedPrediction;
	private final PredictionResultData predictionResultData;
	
	public PredictionResultLogEvent(@NotNull IMiner miner, @Nullable Streamer streamer, @Nullable PlacedPrediction placedPrediction, @NotNull PredictionResultData predictionResultData){
		super(miner, streamer);
		this.placedPrediction = placedPrediction;
		this.predictionResultData = predictionResultData;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Bet result [%s | %s]".formatted(getType(), getGain());
	}
	
	@NotNull
	private PredictionResultType getType(){
		var result = Optional.ofNullable(predictionResultData.getPrediction().getResult());
		return result.map(PredictionResultPayload::getType).orElse(PredictionResultType.UNKNOWN);
	}
	
	private String getGain(){
		if(getType() == PredictionResultType.REFUND){
			return "0";
		}
		
		var result = Optional.ofNullable(predictionResultData.getPrediction().getResult());
		var pointsWon = result.map(PredictionResultPayload::getPointsWon).orElse(0);
		
		return Optional.ofNullable(placedPrediction)
				.map(prediction -> pointsWon - prediction.getAmount())
				.map(value -> millify(value, true))
				.orElse("Unknown final gain, obtained %s points".formatted(millify(pointsWon, true)));
	}
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "🧧";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_PREDICTION;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Bet result";
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Type").value(getType().toString()).build(),
				Field.builder().name("Points gained").value(getGain()).build());
	}
}
