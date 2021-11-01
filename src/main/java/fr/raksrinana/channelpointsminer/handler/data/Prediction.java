package fr.raksrinana.twitchminer.handler.data;

import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Prediction{
	@NotNull
	private String streamerId;
	@NotNull
	private Event event;
	@Setter
	private boolean scheduled = false;
}
