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
public class StreamerSettings implements Cloneable{
	@JsonProperty("makePredictions")
	@Builder.Default
	private boolean makePredictions = false;
	@JsonProperty("followRaid")
	@Builder.Default
	private boolean followRaid = false;
	@JsonProperty("priorities")
	@Builder.Default
	@NotNull
	private List<StreamerPriority> priorities = new ArrayList<>();
	
	@Override
	public StreamerSettings clone() throws CloneNotSupportedException{
		return (StreamerSettings) super.clone();
	}
}
