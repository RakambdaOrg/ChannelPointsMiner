package fr.raksrinana.twitchminer.api.kraken.data.follows;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class GetFollowsResponse{
	@JsonProperty("_total")
	private int total;
	@JsonProperty("follows")
	private List<Follow> follows;
}
