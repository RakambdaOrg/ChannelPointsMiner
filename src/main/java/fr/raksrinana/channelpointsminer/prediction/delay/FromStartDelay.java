package fr.raksrinana.twitchminer.prediction.delay;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Event;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@JsonTypeName("fromStart")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class FromStartDelay implements DelayCalculator{
	private int seconds;
	
	@Override
	@NotNull
	public ZonedDateTime calculate(@NotNull Event event){
		return event.getCreatedAt().plusSeconds(seconds);
	}
}
