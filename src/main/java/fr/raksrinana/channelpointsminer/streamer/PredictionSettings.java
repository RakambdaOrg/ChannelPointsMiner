package fr.raksrinana.twitchminer.streamer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.prediction.DelayCalculator;
import fr.raksrinana.twitchminer.prediction.FromEndDelay;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PredictionSettings{
	@JsonProperty("delay")
	@NotNull
	@Builder.Default
	private DelayCalculator delay = FromEndDelay.builder().seconds(10).build();
	@JsonProperty("minimumPointsRequired")
	private int minimumPointsRequired = 0;
	
	public PredictionSettings(PredictionSettings origin){
		delay = origin.delay;
		minimumPointsRequired = origin.minimumPointsRequired;
	}
}
