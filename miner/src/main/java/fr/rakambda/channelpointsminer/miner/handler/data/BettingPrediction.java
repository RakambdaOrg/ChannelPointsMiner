package fr.rakambda.channelpointsminer.miner.handler.data;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class BettingPrediction{
	@NotNull
	private Streamer streamer;
	@Setter
	@NotNull
	private Event event;
	@Setter
	@NotNull
	private ZonedDateTime lastUpdate;
	@Setter
	@Builder.Default
	private PredictionState state = PredictionState.CREATED;
}
