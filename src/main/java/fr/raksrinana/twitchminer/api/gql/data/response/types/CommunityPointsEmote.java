package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("CommunityPointsEmote")
@Getter
@AllArgsConstructor
public class CommunityPointsEmote extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("token")
	private String token;
	
	public CommunityPointsEmote(){
		super("CommunityPointsEmote");
	}
}
