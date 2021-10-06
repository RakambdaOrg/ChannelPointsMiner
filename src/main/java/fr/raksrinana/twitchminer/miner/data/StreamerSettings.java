package fr.raksrinana.twitchminer.miner.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class StreamerSettings implements Cloneable{
	@JsonProperty("makePredictions")
	private boolean makePredictions = false;
	@JsonProperty("followRaid")
	private boolean followRaid;
	
	@Override
	public StreamerSettings clone() throws CloneNotSupportedException{
		return (StreamerSettings) super.clone();
	}
}
