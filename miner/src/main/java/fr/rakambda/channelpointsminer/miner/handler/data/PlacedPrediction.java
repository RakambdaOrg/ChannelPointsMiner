package fr.rakambda.channelpointsminer.miner.handler.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class PlacedPrediction{
	@NonNull
	private String eventId;
	@Nullable
	private BettingPrediction bettingPrediction;
	private int amount;
	@NonNull
	private String outcomeId;
	@NonNull
	private Instant predictedAt;
}
