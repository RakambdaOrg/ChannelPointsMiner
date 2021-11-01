package fr.raksrinana.twitchminer.miner.streamer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.miner.priority.StreamerPriority;
import lombok.*;
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
public class StreamerSettings{
	@JsonProperty("makePredictions")
	@Builder.Default
	private boolean makePredictions = false;
	@JsonProperty("followRaid")
	@Builder.Default
	private boolean followRaid = false;
	@JsonProperty("participateCampaigns")
	@Builder.Default
	private boolean participateCampaigns = false;
	@JsonProperty("priorities")
	@Builder.Default
	@NotNull
	private List<StreamerPriority> priorities = new ArrayList<>();
	
	public StreamerSettings(@NotNull StreamerSettings origin){
		this();
		makePredictions = origin.makePredictions;
		followRaid = origin.followRaid;
		participateCampaigns = origin.participateCampaigns;
		priorities.addAll(origin.priorities);
	}
}
