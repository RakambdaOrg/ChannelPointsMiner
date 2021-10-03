package fr.raksrinana.twitchminer.api.gql.data.response.channelpointscontext;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.response.types.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChannelPointsContextData{
	@JsonProperty("community")
	private User community;
	@JsonProperty("currentUser")
	private User currentUser;
}
