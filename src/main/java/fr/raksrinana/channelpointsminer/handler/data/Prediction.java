package fr.raksrinana.twitchminer.handler.data;

import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import fr.raksrinana.twitchminer.streamer.Streamer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Prediction{
	@NotNull
	private Streamer streamer;
	@Setter
	@NotNull
	private Event event;
	@Setter
	@NotNull
	private ZonedDateTime lastUpdate;
	@Setter
	private boolean scheduled = false;
}