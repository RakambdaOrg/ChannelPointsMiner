package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("RequestInfo")
@Getter
public class RequestInfo extends GQLType{
	@JsonProperty("countryCode")
	@NotNull
	private String countryCode;
	
	public RequestInfo(){
		super("RequestInfo");
	}
}
