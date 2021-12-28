package fr.raksrinana.channelpointsminer.handler.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class PlacedPrediction{
	@NotNull
	private String eventId;
	@Nullable
	private BettingPrediction bettingPrediction;
	private int amount;
	@NotNull
	private String outcomeId;
}
