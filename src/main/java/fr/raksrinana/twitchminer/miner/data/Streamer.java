package fr.raksrinana.twitchminer.miner.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.BroadcastSettings;
import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.gql.data.types.Stream;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.twitchminer.api.Constants.TWITCH_URL;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Log4j2
public class Streamer{
	@NotNull
	@Getter
	@EqualsAndHashCode.Include
	private final String id;
	@NotNull
	@Getter
	@ToString.Include
	private final String username;
	@NotNull
	@Getter
	private StreamerSettings settings;
	
	@JsonProperty("url")
	private URL url;
	
	@Nullable
	@Setter
	private ChannelPointsContextData channelPointsContext;
	@Nullable
	@Setter
	private VideoPlayerStreamInfoOverlayChannelData videoPlayerStreamInfoOverlayChannel;
	@Nullable
	@Setter
	private DropsHighlightServiceAvailableDropsData dropsHighlightServiceAvailableDrops;
	@Nullable
	@Setter
	@Getter
	private URL spadeUrl;
	
	public boolean updateCampaigns(){
		return true;
	}
	
	public Optional<Game> getGame(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::getBroadcastSettings)
				.map(BroadcastSettings::getGame);
	}
	
	public Optional<String> getBroadcastId(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::getStream)
				.map(Stream::getId);
	}
	
	@Nullable
	public URL getUrl(){
		if(Objects.isNull(url)){
			try{
				url = new URL(TWITCH_URL, getUsername());
			}
			catch(MalformedURLException e){
				log.error("Failed to construct streamer url", e);
			}
		}
		return url;
	}
	
	public boolean isStreamingGame(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::getBroadcastSettings)
				.map(BroadcastSettings::getGame)
				.map(game -> Objects.nonNull(game.getName()))
				.orElse(false);
	}
	
	public boolean isStreaming(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::isStreaming)
				.orElse(false);
	}
}
