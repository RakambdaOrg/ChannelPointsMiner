package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("UserSelfConnection")
@Getter
@AllArgsConstructor
public class UserSelfConnection extends GQLType{
	@JsonProperty("isModerator")
	private boolean moderator;
	
	public UserSelfConnection(){
		super("UserSelfConnection");
	}
}
