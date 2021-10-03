package fr.raksrinana.twitchminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class GQLError{
	@JsonProperty("message")
	@NotNull
	private String message;
	@JsonProperty("locations")
	@NotNull
	private List<Location> locations = new LinkedList<>();
}
