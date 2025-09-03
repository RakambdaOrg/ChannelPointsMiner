package fr.rakambda.channelpointsminer.miner.api.gql.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class GQLError{
	@JsonProperty("message")
	@NonNull
	private String message;
	@JsonProperty("locations")
	@NonNull
	@Builder.Default
	private List<Location> locations = new ArrayList<>();
	@JsonProperty("path")
	@NonNull
	@Builder.Default
	private List<String> path = new ArrayList<>();
}
