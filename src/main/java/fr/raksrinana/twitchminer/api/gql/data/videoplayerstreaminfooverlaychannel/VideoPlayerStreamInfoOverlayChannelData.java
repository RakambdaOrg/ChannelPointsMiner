package fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VideoPlayerStreamInfoOverlayChannelData{
	@JsonProperty("user")
	private User user;
}
