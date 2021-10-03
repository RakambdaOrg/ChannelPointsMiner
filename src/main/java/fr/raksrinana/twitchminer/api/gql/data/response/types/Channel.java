package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("Channel")
@Getter
@AllArgsConstructor
public class Channel extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("self")
	private ChannelSelfEdge self;
	@JsonProperty("communityPointsSettings")
	private CommunityPointsChannelSettings communityPointsSettings;
	
	public Channel(){
		super("Channel");
	}
}
