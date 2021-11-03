package fr.raksrinana.twitchminer.api.gql.data.makeprediction;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.MakePredictionPayload;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class MakePredictionData{
	@JsonProperty("makePrediction")
	@NotNull
	private MakePredictionPayload makePrediction;
}
