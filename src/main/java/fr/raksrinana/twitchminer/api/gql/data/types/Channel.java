package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@JsonTypeName("Channel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class Channel extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("self")
	@Nullable
	private ChannelSelfEdge self;
	@JsonProperty("communityPointsSettings")
	@Nullable
	private CommunityPointsChannelSettings communityPointsSettings;
	@JsonProperty("viewerDropCampaigns")
	@Nullable
	private List<DropCampaign> viewerDropCampaigns;
	
	@NotNull
	public Optional<CommunityPointsClaim> getClaim(){
		return Optional.ofNullable(self).flatMap(ChannelSelfEdge::getClaim);
	}
	
	@NotNull
	public Optional<Collection<CommunityPointsMultiplier>> getMultipliers(){
		return Optional.ofNullable(self).map(ChannelSelfEdge::getMultipliers);
	}
}
