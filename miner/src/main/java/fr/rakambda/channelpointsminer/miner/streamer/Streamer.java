package fr.rakambda.channelpointsminer.miner.streamer;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.BroadcastSettings;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ChannelSelfEdge;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsClaim;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsMultiplier;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsProperties;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Game;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Stream;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.priority.IStreamerPriority;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Log4j2
public class Streamer{
	public static final Duration SEVEN_MINUTES = Duration.ofMinutes(7);
	
	@NonNull
	@Getter
	@EqualsAndHashCode.Include
	private final String id;
	@NonNull
	@Getter
	@ToString.Include
	private final String username;
	@NonNull
	@Getter
	@Setter
	private StreamerSettings settings;
	@Setter
	private Instant lastUpdated = Instant.EPOCH;
	@Setter
	private Instant lastOffline = Instant.EPOCH;
	@Getter
	private Duration watchedDuration = Duration.ZERO;
	
	private URL channelUrl;
	
	@Nullable
	@Setter
	private ChannelPointsContextData channelPointsContext;
	@Nullable
	@Setter
	private VideoPlayerStreamInfoOverlayChannelData videoPlayerStreamInfoOverlayChannel;
	@Nullable
	@Setter
	@Getter
	private DropsHighlightServiceAvailableDropsData dropsHighlightServiceAvailableDrops;
	@Nullable
	@Setter
	@Getter
	private URL spadeUrl;
	@Getter
	private boolean chatBanned;
	@Nullable
	@Setter
	@Getter
	private URL m3u8Url;
	
	public void addWatchedDuration(@NonNull Duration duration){
		watchedDuration = watchedDuration.plus(duration);
	}
	
	public void resetWatchedDuration(){
		watchedDuration = Duration.ZERO;
	}
	
	public boolean mayClaimStreak(){
		return lastOffline.plus(30, MINUTES).isBefore(TimeFactory.now())
				&& getWatchedDuration().compareTo(SEVEN_MINUTES) < 0;
	}
	
	public boolean followRaids(){
		return settings.isFollowRaid();
	}
	
	public boolean isExcludeSubscriberDrops(){
		return settings.isExcludeSubscriberDrops();
	}
	
	public boolean needUpdate(){
		return TimeFactory.now().isAfter(lastUpdated.plus(5, MINUTES));
	}
	
	public int getScore(@NonNull IMiner miner, Predicate<IStreamerPriority> filterPriority){
		try(var ignored = LogContext.with(miner).withStreamer(this)){
			var score = settings.getPriorities().stream()
					.filter(filterPriority)
					.mapToInt(p -> {
						var s = p.getScore(miner, this);
						if(s != 0){
							log.trace("Obtained score of {} from {}", s, p);
						}
						return s;
					})
					.sum();
			log.debug("Calculated score of {}", score);
			return score;
		}
	}
	
	public Collection<CommunityPointsMultiplier> getActiveMultipliers(){
		return ofNullable(channelPointsContext)
				.map(ChannelPointsContextData::getCommunity)
				.map(User::getChannel)
				.map(Channel::getSelf)
				.map(ChannelSelfEdge::getCommunityPoints)
				.map(CommunityPointsProperties::getActiveMultipliers)
				.orElse(List.of());
	}
	
	@NonNull
	public Optional<Integer> getChannelPoints(){
		return ofNullable(channelPointsContext)
				.map(ChannelPointsContextData::getCommunity)
				.map(User::getChannel)
				.map(Channel::getSelf)
				.map(ChannelSelfEdge::getCommunityPoints)
				.map(CommunityPointsProperties::getBalance);
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
	
	public Optional<String> getClaimId(){
		return ofNullable(channelPointsContext)
				.map(ChannelPointsContextData::getCommunity)
				.map(User::getChannel)
				.map(Channel::getSelf)
				.map(ChannelSelfEdge::getCommunityPoints)
				.map(CommunityPointsProperties::getAvailableClaim)
				.map(CommunityPointsClaim::getId);
	}
	
	@NonNull
	public Optional<Game> getGame(){
		return ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::getBroadcastSettings)
				.map(BroadcastSettings::getGame);
	}
	
	public int getIndex(){
		return settings.getIndex();
	}
	
	@NonNull
	public Optional<String> getStreamId(){
		return getStream().map(Stream::getId);
	}
	
	@NonNull
	private Optional<Stream> getStream(){
		return ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::getStream);
	}
	
	@NonNull
	public Optional<URL> getProfileImage(){
		return Optional.ofNullable(videoPlayerStreamInfoOverlayChannel)
				.map(VideoPlayerStreamInfoOverlayChannelData::getUser)
				.map(User::getProfileImageUrl);
	}
	
	public boolean isParticipateCampaigns(){
		return settings.isParticipateCampaigns();
	}
	
	public boolean isDismissKnownGlobalCampaigns(){
		return settings.isDismissKnownGlobalCampaigns();
	}
	
	public boolean isStreamingGame(){
		return getGame()
				.map(Game::getName)
				.map(game -> !game.isBlank())
				.orElse(false);
	}
	
	public boolean isStreaming(){
		return getStream().isPresent();
	}
	
	public void setChatBanned(boolean chatBanned){
		if(chatBanned && !this.chatBanned){
			log.warn("Chat banned for streamer {}", this);
		}
		this.chatBanned = chatBanned;
	}
	
	public boolean hasStreamedEnoughTime(){
		return getWatchedDuration().compareTo(Streamer.SEVEN_MINUTES) > 0;
	}
}
