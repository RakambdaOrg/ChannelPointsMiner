package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@JsonTypeName("Game")
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class Game extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("displayName")
	private String displayName;
	@JsonProperty("name")
	private String name;
	
	public Game(){
		super("Game");
	}
}
