package fr.raksrinana.twitchminer.api.gql.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class GQLResponse<T>{
	@JsonProperty("errors")
	@NotNull
	private List<GQLError> errors = new LinkedList<>();
	@JsonProperty("extensions")
	@NotNull
	private Map<String, Object> extensions = new HashMap<>();
	@JsonProperty("data")
	@Nullable
	private T data;
}
