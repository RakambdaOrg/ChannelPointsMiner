package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__typename")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = RequestInfo.class, name = "RequestInfo"),
		@JsonSubTypes.Type(value = User.class, name = "User")
})
public abstract class GQLType{
	@JsonProperty("__typename")
	@NotNull
	private String typename;
}
