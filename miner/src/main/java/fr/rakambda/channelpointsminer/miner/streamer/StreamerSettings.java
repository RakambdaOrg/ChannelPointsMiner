package fr.rakambda.channelpointsminer.miner.streamer;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import fr.rakambda.channelpointsminer.miner.priority.IStreamerPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonClassDescription("Streamer settings")
public class StreamerSettings{
	@JsonProperty("enabled")
	@JsonPropertyDescription("Enable mining for this streamer. Default: true")
	@Builder.Default
	private boolean enabled = true;
	@JsonProperty("makePredictions")
	@JsonPropertyDescription("Place predictions. Default: false")
	@Builder.Default
	private boolean makePredictions = false;
	@JsonProperty("followRaid")
	@JsonPropertyDescription("Follow raids to get bonus points. Default: false")
	@Builder.Default
	private boolean followRaid = false;
	@JsonProperty("participateCampaigns")
	@JsonPropertyDescription("Participate in campaigns and claim rewards (drops). Default: false")
	@Builder.Default
	private boolean participateCampaigns = false;
	@JsonProperty("dismissKnownGlobalCampaigns")
	@JsonPropertyDescription("Dismiss known global campaigns that blocks other drops from being seen. Default: false")
	@Builder.Default
	private boolean dismissKnownGlobalCampaigns = false;
	@JsonProperty("claimMoments")
	@JsonPropertyDescription("Claim moments. Default: false")
	@Builder.Default
	private boolean claimMoments = false;
	@JsonProperty("joinChat")
	@JsonPropertyDescription("Join chat. Default: false")
	@Builder.Default
	private boolean joinIrc = false;
	@JsonProperty("excludeSubscriberDrops")
	@JsonPropertyDescription("Exclude progressing drops that require subscriptions. Default: true")
	@Builder.Default
	private boolean excludeSubscriberDrops = true;
	@JsonProperty("index")
	@JsonPropertyDescription("The streamer index. This value is used when streamers have the same score from the defined priorities, the one with the lowest index will be picked first. Default: 2147483647")
	@Builder.Default
	private int index = Integer.MAX_VALUE;
	@JsonProperty("predictions")
	@JsonPropertyDescription("Prediction settings.")
	@JsonMerge
	@NotNull
	@Builder.Default
	private PredictionSettings predictions = new PredictionSettings();
	@JsonProperty("priorities")
	@JsonPropertyDescription("A list of conditions that, if met, will prioritize this streamer.")
	@Builder.Default
	@NotNull
	private List<IStreamerPriority> priorities = new ArrayList<>();
	
	public StreamerSettings(@NotNull StreamerSettings origin){
		this();
		enabled = origin.enabled;
		makePredictions = origin.makePredictions;
		followRaid = origin.followRaid;
		participateCampaigns = origin.participateCampaigns;
		dismissKnownGlobalCampaigns = origin.dismissKnownGlobalCampaigns;
		joinIrc = origin.joinIrc;
		excludeSubscriberDrops = origin.excludeSubscriberDrops;
		index = origin.index;
		predictions = new PredictionSettings(origin.predictions);
		priorities.addAll(origin.priorities);
		claimMoments = origin.claimMoments;
	}
}
