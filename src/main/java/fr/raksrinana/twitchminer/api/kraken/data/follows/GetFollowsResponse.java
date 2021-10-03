package fr.raksrinana.twitchminer.api.kraken.data.follows;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetFollowsResponse{
	@JsonProperty("_total")
	private int total;
	@JsonProperty("follows")
	private List<Follow> follows;
}
