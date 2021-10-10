package fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.gql.data.types.Stream;
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
public class VideoPlayerStreamInfoOverlayChannelData{
	@JsonProperty("user")
	@NotNull
	private User user;
	
	public Optional<Stream> getStream(){
		return Optional.ofNullable(user.getStream());
	}
	
	public Optional<Game> getGame(){
		return user.getGame();
	}
}
