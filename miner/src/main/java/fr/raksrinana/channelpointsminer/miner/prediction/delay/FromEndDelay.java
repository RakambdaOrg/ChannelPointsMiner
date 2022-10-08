package fr.raksrinana.channelpointsminer.miner.prediction.delay;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@JsonTypeName("fromEnd")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@JsonClassDescription("Place the bet a certain amount of time before the end of the original prediction.")
public class FromEndDelay implements IDelayCalculator{
	@JsonProperty(value = "seconds", required = true)
	@JsonPropertyDescription("Number of seconds before the end to place the bet.")
	private int seconds;
	
	@Override
	@NotNull
	public ZonedDateTime calculate(@NotNull Event event){
		return event.getCreatedAt()
				.plusSeconds(event.getPredictionWindowSeconds())
				.minusSeconds(seconds);
	}
}
