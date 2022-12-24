package fr.rakambda.channelpointsminer.miner.prediction.delay;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@JsonTypeName("percentage")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@JsonClassDescription("Place the bet after 'percent'% of the original timer elapsed.")
public class PercentageDelay implements IDelayCalculator{
	@JsonProperty("percent")
	@JsonPropertyDescription("The percentage of the timer, as a decimal value, between 0 and 1.")
	private float percent;
	
	@Override
	@NotNull
	public ZonedDateTime calculate(@NotNull Event event){
		return event.getCreatedAt().plusSeconds((long) (percent * event.getPredictionWindowSeconds()));
	}
}
