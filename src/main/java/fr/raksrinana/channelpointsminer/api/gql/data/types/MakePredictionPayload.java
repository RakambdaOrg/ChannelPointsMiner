package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("MakePredictionPayload")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class MakePredictionPayload extends GQLType{
	@JsonProperty("error")
	@Nullable
	private MakePredictionError error;
}
