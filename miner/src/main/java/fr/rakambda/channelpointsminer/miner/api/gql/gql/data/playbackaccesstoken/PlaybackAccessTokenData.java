package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.playbackaccesstoken;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.StreamPlaybackAccessToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PlaybackAccessTokenData{
	@JsonProperty("streamPlaybackAccessToken")
	@NotNull
	private StreamPlaybackAccessToken streamPlaybackAccessToken;
}
