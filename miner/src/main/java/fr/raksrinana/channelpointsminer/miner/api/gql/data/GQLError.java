package fr.raksrinana.channelpointsminer.miner.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class GQLError{
	@JsonProperty("message")
	@NotNull
	private String message;
	@JsonProperty("locations")
	@NotNull
	@Builder.Default
	private List<Location> locations = new LinkedList<>();
	@JsonProperty("path")
	@NotNull
	@Builder.Default
	private List<String> path = new LinkedList<>();
}
