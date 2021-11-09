package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("MakePredictionError")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class MakePredictionError extends GQLType{
	@JsonProperty("code")
	@NotNull
	private fr.raksrinana.channelpointsminer.api.gql.data.types.MakePredictionErrorCode code;
}
