package fr.raksrinana.channelpointsminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class GQLResponse<T>{
	@JsonProperty("errors")
	@NotNull
	@Builder.Default
	private List<GQLError> errors = new LinkedList<>();
	@JsonProperty("extensions")
	@NotNull
	@Builder.Default
	private Map<String, Object> extensions = new HashMap<>();
	@JsonProperty("data")
	@Nullable
	private T data;
	@JsonProperty("error")
	@Nullable
	private String error;
	@JsonProperty("status")
	@Nullable
	private Integer status;
	@JsonProperty("message")
	@Nullable
	private String message;
	
	public boolean isError(){
		return Objects.nonNull(error) || !errors.isEmpty();
	}
}
