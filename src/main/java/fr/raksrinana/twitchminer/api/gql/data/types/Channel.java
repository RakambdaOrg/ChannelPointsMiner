package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

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
	@Builder.Default
	private List<DropCampaign> viewerDropCampaigns = new ArrayList<>();
}
