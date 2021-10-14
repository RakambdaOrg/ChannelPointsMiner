package fr.raksrinana.twitchminer.api.gql.data.channelpointscontext;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsClaim;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ChannelPointsContextData{
	@JsonProperty("community")
	@NotNull
	private User community;
	@JsonProperty("currentUser")
	@NotNull
	private User currentUser;
	
	public Optional<CommunityPointsClaim> getClaim(){
		return community.getClaim();
	}
}
