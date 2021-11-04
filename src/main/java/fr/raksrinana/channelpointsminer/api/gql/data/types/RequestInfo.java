package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("RequestInfo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class RequestInfo extends GQLType{
	@JsonProperty("countryCode")
	@NotNull
	private String countryCode;
}
