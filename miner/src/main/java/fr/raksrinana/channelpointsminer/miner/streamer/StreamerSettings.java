package fr.raksrinana.channelpointsminer.miner.streamer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.priority.IStreamerPriority;
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
public class StreamerSettings{
	@JsonProperty("enabled")
	@Builder.Default
	private boolean enabled = true;
	@JsonProperty("makePredictions")
	@Builder.Default
	private boolean makePredictions = false;
	@JsonProperty("followRaid")
	@Builder.Default
	private boolean followRaid = false;
	@JsonProperty("participateCampaigns")
	@Builder.Default
	private boolean participateCampaigns = false;
	@JsonProperty("joinIrc")
	@Builder.Default
	private boolean joinIrc = false;
	@JsonProperty("index")
	@Builder.Default
	private int index = Integer.MAX_VALUE;
	@JsonProperty("predictions")
	@NotNull
	@Builder.Default
	private PredictionSettings predictions = new PredictionSettings();
	@JsonProperty("priorities")
	@Builder.Default
	@NotNull
	private List<IStreamerPriority> priorities = new ArrayList<>();
	
	public StreamerSettings(@NotNull StreamerSettings origin){
		this();
		enabled = origin.enabled;
		makePredictions = origin.makePredictions;
		followRaid = origin.followRaid;
		participateCampaigns = origin.participateCampaigns;
		joinIrc = origin.joinIrc;
		index = origin.index;
		predictions = new PredictionSettings(origin.predictions);
		priorities.addAll(origin.priorities);
	}
}
