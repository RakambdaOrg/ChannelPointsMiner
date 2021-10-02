package fr.raksrinana.twitchminer.api.helix.data.follows;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.helix.data.Pagination;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class GetFollowsResponse{
	@JsonProperty("total")
	private int total;
	@JsonProperty("data")
	private List<Follow> follows;
	@JsonProperty("pagination")
	private Pagination pagination;
}
