package fr.raksrinana.twitchminer.api.gql.data.channelpointscontext;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelPointsContextData{
	@JsonProperty("community")
	private User community;
	@JsonProperty("currentUser")
	private User currentUser;
}
