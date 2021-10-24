package fr.raksrinana.twitchminer.miner.streamer;

import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.miner.priority.StreamerPriority;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamerTest{
	private static final String USERNAME = "username";
	
	private Streamer tested;
	
	@Mock
	private StreamerSettings settings;
	@Mock
	private VideoPlayerStreamInfoOverlayChannelData videoPlayerStreamInfoOverlayChannelData;
	@Mock
	private ChannelPointsContextData channelPointsContextData;
	@Mock
	private Game game;
	@Mock
	private User user;
	@Mock
	private Stream stream;
	@Mock
	private BroadcastSettings broadcastSettings;
	@Mock
	private Channel channel;
	@Mock
	private ChannelSelfEdge channelSelfEdge;
	@Mock
	private CommunityPointsProperties communityPointsProperties;
	
	@BeforeEach
	void setUp(){
		tested = new Streamer("streamer-id", USERNAME, settings);
		tested.setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
	}
	
	@Test
	void getGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getBroadcastSettings()).thenReturn(broadcastSettings);
		when(broadcastSettings.getGame()).thenReturn(game);
		
		assertThat(tested.getGame()).isPresent().get().isEqualTo(game);
	}
	
	@Test
	void getGameNoGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getBroadcastSettings()).thenReturn(broadcastSettings);
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getGameNoBroadcastSettings(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getGameNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getStreamId(){
		var streamId = "stream-id";
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getStream()).thenReturn(stream);
		when(stream.getId()).thenReturn(streamId);
		
		assertThat(tested.getStreamId()).isPresent().get().isEqualTo(streamId);
	}
	
	@Test
	void getStreamIdNoStream(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		
		assertThat(tested.getStreamId()).isEmpty();
	}
	
	@Test
	void getStreamIdNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.getStreamId()).isEmpty();
	}
	
	@Test
	void getChannelUrl() throws MalformedURLException{
		assertThat(tested.getChannelUrl()).isEqualTo(new URL("https://www.twitch.tv/" + USERNAME));
	}
	
	@Test
	void streaming(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getStream()).thenReturn(stream);
		
		assertThat(tested.isStreaming()).isTrue();
	}
	
	@Test
	void notStreaming(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getStream()).thenReturn(null);
		
		assertThat(tested.isStreaming()).isFalse();
	}
	
	@Test
	void streamingNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.isStreaming()).isFalse();
	}
	
	@Test
	void streamingGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getBroadcastSettings()).thenReturn(broadcastSettings);
		when(broadcastSettings.getGame()).thenReturn(game);
		when(game.getName()).thenReturn("GAME");
		
		assertThat(tested.isStreamingGame()).isTrue();
	}
	
	@Test
	void streamingGameNoGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getBroadcastSettings()).thenReturn(broadcastSettings);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
	
	@Test
	void streamingGameNoBroadcastSettings(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
	
	@Test
	void streamingGameNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"    "})
	void notStreamingGameNoName(String name){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.getBroadcastSettings()).thenReturn(broadcastSettings);
		when(broadcastSettings.getGame()).thenReturn(game);
		when(game.getName()).thenReturn(name);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
	
	@Test
	void getClaimId(){
		var id = "clam-id";
		var claim = mock(CommunityPointsClaim.class);
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		when(channel.getSelf()).thenReturn(channelSelfEdge);
		when(channelSelfEdge.getCommunityPoints()).thenReturn(communityPointsProperties);
		when(communityPointsProperties.getAvailableClaim()).thenReturn(claim);
		when(claim.getId()).thenReturn(id);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getClaimId()).isPresent()
				.get().isEqualTo(id);
	}
	
	@Test
	void getClaimIdNoClaim(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		when(channel.getSelf()).thenReturn(channelSelfEdge);
		when(channelSelfEdge.getCommunityPoints()).thenReturn(communityPointsProperties);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getClaimId()).isEmpty();
	}
	
	@Test
	void getClaimIdNoSelf(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getClaimId()).isEmpty();
	}
	
	@Test
	void getClaimIdNoChannel(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getClaimId()).isEmpty();
	}
	
	@Test
	void getClaimIdNull(){
		tested.setChannelPointsContext(null);
		
		assertThat(tested.getClaimId()).isEmpty();
	}
	
	@Test
	void getChannelPoints(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		when(channel.getSelf()).thenReturn(channelSelfEdge);
		when(channelSelfEdge.getCommunityPoints()).thenReturn(communityPointsProperties);
		when(communityPointsProperties.getBalance()).thenReturn(50);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getChannelPoints()).isPresent()
				.get().isEqualTo(50);
	}
	
	@Test
	void getChannelPointsNoCommunityPoints(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		when(channel.getSelf()).thenReturn(channelSelfEdge);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getChannelPoints()).isEmpty();
	}
	
	@Test
	void getChannelPointsNoSelf(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getChannelPoints()).isEmpty();
	}
	
	@Test
	void getChannelPointsNoChannel(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getChannelPoints()).isEmpty();
	}
	
	@Test
	void getChannelPointsNull(){
		tested.setChannelPointsContext(null);
		
		assertThat(tested.getChannelPoints()).isEmpty();
	}
	
	@Test
	void getMultipliers(){
		var multiplier = mock(CommunityPointsMultiplier.class);
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		when(channel.getSelf()).thenReturn(channelSelfEdge);
		when(channelSelfEdge.getCommunityPoints()).thenReturn(communityPointsProperties);
		when(communityPointsProperties.getActiveMultipliers()).thenReturn(List.of(multiplier));
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getActiveMultipliers()).containsExactlyInAnyOrder(multiplier);
	}
	
	@Test
	void getMultipliersEmpty(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		when(channel.getSelf()).thenReturn(channelSelfEdge);
		when(channelSelfEdge.getCommunityPoints()).thenReturn(communityPointsProperties);
		when(communityPointsProperties.getActiveMultipliers()).thenReturn(List.of());
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getActiveMultipliers()).isEmpty();
	}
	
	@Test
	void getMultipliersNoSelf(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		when(user.getChannel()).thenReturn(channel);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getActiveMultipliers()).isEmpty();
	}
	
	@Test
	void getMultipliersNoChannel(){
		when(channelPointsContextData.getCommunity()).thenReturn(user);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getActiveMultipliers()).isEmpty();
	}
	
	@Test
	void getMultipliersNull(){
		tested.setChannelPointsContext(null);
		
		assertThat(tested.getActiveMultipliers()).isEmpty();
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			6,
			10,
			15,
			30
	})
	void needUpdate(int before){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			var now = Instant.parse("2020-01-01T12:00:00Z");
			timeFactory.when(TimeFactory::now).thenReturn(now);
			
			tested.setLastUpdated(now.minus(before, MINUTES));
			assertThat(tested.needUpdate()).isTrue();
		}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {
			-5,
			0,
			1,
			2,
			3,
			4,
			5
	})
	void doesNotNeedUpdate(int before){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			var now = Instant.parse("2020-01-01T12:00:00Z");
			timeFactory.when(TimeFactory::now).thenReturn(now);
			
			tested.setLastUpdated(now.minus(before, MINUTES));
			assertThat(tested.needUpdate()).isFalse();
		}
	}
	
	@Test
	void followRaid(){
		when(settings.isFollowRaid()).thenReturn(true);
		assertThat(tested.followRaids()).isTrue();
	}
	
	@Test
	void doesNotFollowRaid(){
		when(settings.isFollowRaid()).thenReturn(false);
		assertThat(tested.followRaids()).isFalse();
	}
	
	@Test
	void getScoreNoPriorities(){
		when(settings.getPriorities()).thenReturn(List.of());
		assertThat(tested.getScore()).isEqualTo(0);
	}
	
	@Test
	void getScoreSumsScores(){
		var s1 = 15;
		var s2 = 17;
		
		var p1 = mock(StreamerPriority.class);
		var p2 = mock(StreamerPriority.class);
		
		when(p1.getScore(tested)).thenReturn(s1);
		when(p2.getScore(tested)).thenReturn(s2);
		
		when(settings.getPriorities()).thenReturn(List.of(p1, p2));
		assertThat(tested.getScore()).isEqualTo(s1 + s2);
	}
}