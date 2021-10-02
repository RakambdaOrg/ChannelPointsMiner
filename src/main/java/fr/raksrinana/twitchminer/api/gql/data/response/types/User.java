package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("User")
@Getter
public class User extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("stream")
	@Nullable
	private Object stream;
	
	public User(){
		super("User");
	}
}
