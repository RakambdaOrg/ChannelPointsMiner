package fr.raksrinana.twitchminer.miner.data;

import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsClaim;
import fr.raksrinana.twitchminer.api.gql.data.types.Game;
import fr.raksrinana.twitchminer.api.gql.data.types.Stream;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import static java.time.temporal.ChronoUnit.MINUTES;

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
	@Setter
	private Instant lastUpdated = Instant.EPOCH;
	
	private URL channelUrl;
	
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
	
	public boolean followRaids(){
		return settings.isFollowRaid();
	}
	
	public Optional<String> getClaimId(){
		return Optional.ofNullable(channelPointsContext)
				.flatMap(ChannelPointsContextData::getClaim)
				.map(CommunityPointsClaim::getId);
	}
	
	public Optional<Game> getGame(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.flatMap(VideoPlayerStreamInfoOverlayChannelData::getGame);
	}
	
	public Optional<String> getStreamId(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.flatMap(VideoPlayerStreamInfoOverlayChannelData::getStream)
				.map(Stream::getId);
	}
	
	@Nullable
	public URL getChannelUrl(){
		if(Objects.isNull(channelUrl)){
			try{
				channelUrl = URI.create("https://www.twitch.tv/").resolve(getUsername()).toURL();
			}
			catch(MalformedURLException e){
				log.error("Failed to construct streamer url", e);
			}
		}
		return channelUrl;
	}
	
	public boolean isStreamingGame(){
		return getGame()
				.map(Game::getName)
				.map(game -> !game.isBlank())
				.orElse(false);
	}
	
	public boolean isStreaming(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::isStreaming)
				.orElse(false);
	}
	
	public boolean needUpdate(){
		return TimeFactory.now().isAfter(lastUpdated.plus(5, MINUTES));
	}
}
