package fr.rakambda.channelpointsminer.miner.event.impl;

import fr.rakambda.channelpointsminer.miner.api.discord.data.Field;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.event.AbstractLoggableStreamerEvent;
import fr.rakambda.channelpointsminer.miner.handler.data.BettingPrediction;
import fr.rakambda.channelpointsminer.miner.handler.data.PlacedPrediction;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PredictionMadeEvent extends AbstractLoggableStreamerEvent{
	private static final String UNKNOWN_OUTCOME = "UnknownOutcome";
	
	@Getter
	private final PlacedPrediction placedPrediction;
	
	public PredictionMadeEvent(@NotNull IMiner miner, @NotNull String streamerId, @Nullable String streamerUsername, @Nullable Streamer streamer, @NotNull PlacedPrediction placedPrediction){
		super(miner, streamerId, streamerUsername, streamer, placedPrediction.getPredictedAt());
		this.placedPrediction = placedPrediction;
	}
	
	@Override
	@NotNull
	public String getAsLog(){
		return "Bet placed [%s | %s]".formatted(millify(placedPrediction.getAmount(), false), getOutcome());
	}
	
	@NotNull
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
	
	@Override
	@NotNull
	protected String getEmoji(){
		return "🪙";
	}
	
	@Override
	protected int getEmbedColor(){
		return COLOR_PREDICTION;
	}
	
	@Override
	@NotNull
	protected String getEmbedDescription(){
		return "Bet placed";
	}
	
	@Override
	@NotNull
	protected Collection<? extends Field> getEmbedFields(){
		return List.of(
				Field.builder().name("Points placed").value(millify(placedPrediction.getAmount(), false)).build(),
				Field.builder().name("Outcome").value(getOutcome()).build());
	}
}
