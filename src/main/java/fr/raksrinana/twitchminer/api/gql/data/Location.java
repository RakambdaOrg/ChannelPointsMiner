package fr.raksrinana.twitchminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Location{
	@JsonProperty("line")
	private int line;
	@JsonProperty("column")
	private int column;
}
