package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@JsonTypeName("CommunityPointsEmote")
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class CommunityPointsEmote extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("token")
	private String token;
	
	public CommunityPointsEmote(){
		super("CommunityPointsEmote");
	}
}
