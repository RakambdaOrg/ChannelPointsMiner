package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("Game")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class Game extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("displayName")
	private String displayName;
	@JsonProperty("name")
	private String name;
}
