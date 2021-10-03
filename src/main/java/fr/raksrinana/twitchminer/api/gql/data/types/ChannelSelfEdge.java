package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("ChannelSelfEdge")
@Getter
@AllArgsConstructor
public class ChannelSelfEdge extends GQLType{
	@JsonProperty("communityPoints")
	private CommunityPointsProperties communityPoints;
	
	public ChannelSelfEdge(){
		super("ChannelSelfEdge");
	}
}
