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
import org.jspecify.annotations.NonNull;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class BettingPrediction{
	@NonNull
	private Streamer streamer;
	@Setter
	@NonNull
	private Event event;
	@Setter
	@NonNull
	private ZonedDateTime lastUpdate;
	@Setter
	@Builder.Default
	private PredictionState state = PredictionState.CREATED;
}
