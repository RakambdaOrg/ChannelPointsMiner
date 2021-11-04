package fr.raksrinana.channelpointsminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

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
