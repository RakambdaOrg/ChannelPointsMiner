package fr.rakambda.channelpointsminer.miner.prediction.delay;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.time.ZonedDateTime;

@JsonTypeName("fromStart")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@JsonClassDescription("Place the bet a certain amount of time after the beginning of the original prediction.")
public class FromStartDelay implements IDelayCalculator{
	@JsonProperty(value = "seconds", required = true)
	@JsonPropertyDescription("Number of seconds after the start to place the bet.")
	private int seconds;
	
	@Override
	@NonNull
	public ZonedDateTime calculate(@NonNull Event event){
		return event.getCreatedAt().plusSeconds(seconds);
	}
}
