package fr.raksrinana.channelpointsminer.prediction.delay;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Event;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = FromEndDelay.class, name = "fromEnd"),
		@JsonSubTypes.Type(value = FromStartDelay.class, name = "fromStart"),
		@JsonSubTypes.Type(value = PercentageDelay.class, name = "percentage"),
})
public interface IDelayCalculator{
	@NotNull
	ZonedDateTime calculate(@NotNull Event event);
}
