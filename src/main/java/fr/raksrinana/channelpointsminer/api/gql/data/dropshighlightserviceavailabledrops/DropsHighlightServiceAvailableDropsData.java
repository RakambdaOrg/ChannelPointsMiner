package fr.raksrinana.channelpointsminer.api.gql.data.dropshighlightserviceavailabledrops;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.api.gql.data.types.Channel;
import fr.raksrinana.channelpointsminer.api.gql.data.types.User;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class DropsHighlightServiceAvailableDropsData{
	@JsonProperty("channel")
	@NotNull
	private Channel channel;
	@JsonProperty("currentUser")
	@NotNull
	private User currentUser;
}
