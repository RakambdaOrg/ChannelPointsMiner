package fr.raksrinana.channelpointsminer.miner.api.gql.data.makeprediction;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.MakePredictionPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
