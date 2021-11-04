package fr.raksrinana.channelpointsminer.api.helix.data.follows;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.api.helix.data.Pagination;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

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
