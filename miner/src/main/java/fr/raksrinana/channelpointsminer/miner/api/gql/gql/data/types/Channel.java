package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
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
	@JsonProperty("name")
	@Nullable
	private String name;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL url;
	@JsonProperty("self")
	@Nullable
	private ChannelSelfEdge self;
	@JsonProperty("communityPointsSettings")
	@Nullable
	private CommunityPointsChannelSettings communityPointsSettings;
	@JsonProperty("viewerDropCampaigns")
	@NotNull
	@Builder.Default
	private List<DropCampaign> viewerDropCampaigns = new ArrayList<>();
}
