package fr.raksrinana.channelpointsminer.api.gql.data.channelfollows;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.api.gql.data.types.User;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ChannelFollowsData{
	@JsonProperty("user")
	@NotNull
	private User user;
}
