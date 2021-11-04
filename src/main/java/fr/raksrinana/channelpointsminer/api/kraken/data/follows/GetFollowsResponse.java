package fr.raksrinana.channelpointsminer.api.kraken.data.follows;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetFollowsResponse{
	@JsonProperty("_total")
	private int total;
	@JsonProperty("follows")
	@NotNull
	private List<Follow> follows;
}
