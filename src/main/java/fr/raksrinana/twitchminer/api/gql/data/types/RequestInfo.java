package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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
