package fr.raksrinana.channelpointsminer.api.helix.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Pagination{
	@JsonProperty("cursor")
	private String cursor;
}
