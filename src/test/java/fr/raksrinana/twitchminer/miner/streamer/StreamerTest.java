package fr.raksrinana.twitchminer.miner.streamer;

import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.twitchminer.factory.TimeFactory;
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
import java.util.Optional;
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
	
	@BeforeEach
	void setUp(){
		tested = new Streamer("streamer-id", USERNAME, settings);
		tested.setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
	}
	
	@Test
	void getGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getGame()).thenReturn(Optional.of(game));
		
		assertThat(tested.getGame()).isPresent().get().isEqualTo(game);
	}
	
	@Test
	void getGameNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.getGame()).isEmpty();
	}
	
	@Test
	void getStreamId(){
		var streamId = "stream-id";
		when(videoPlayerStreamInfoOverlayChannelData.getStream()).thenReturn(Optional.of(Stream.builder().id(streamId).build()));
		
		assertThat(tested.getStreamId()).isPresent().get().isEqualTo(streamId);
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
		when(user.isStreaming()).thenReturn(true);
		
		assertThat(tested.isStreaming()).isTrue();
	}
	
	@Test
	void streamingNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.isStreaming()).isFalse();
	}
	
	@Test
	void notStreaming(){
		when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		when(user.isStreaming()).thenReturn(false);
		
		assertThat(tested.isStreaming()).isFalse();
	}
	
	@Test
	void streamingGame(){
		when(videoPlayerStreamInfoOverlayChannelData.getGame()).thenReturn(Optional.of(game));
		when(game.getName()).thenReturn("GAME");
		
		assertThat(tested.isStreamingGame()).isTrue();
	}
	
	@Test
	void streamingGameNoData(){
		tested.setVideoPlayerStreamInfoOverlayChannel(null);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"    "})
	void notStreamingGame(String name){
		when(videoPlayerStreamInfoOverlayChannelData.getGame()).thenReturn(Optional.of(game));
		when(game.getName()).thenReturn(name);
		
		assertThat(tested.isStreamingGame()).isFalse();
	}
	
	@Test
	void getClaimId(){
		var id = "clam-id";
		var claim = mock(CommunityPointsClaim.class);
		when(channelPointsContextData.getClaim()).thenReturn(Optional.of(claim));
		when(claim.getId()).thenReturn(id);
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getClaimId()).isPresent()
				.get().isEqualTo(id);
	}
	
	@Test
	void getClaimIdEmpty(){
		when(channelPointsContextData.getClaim()).thenReturn(Optional.empty());
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getClaimId()).isEmpty();
	}
	
	@Test
	void getClaimIdNull(){
		tested.setChannelPointsContext(null);
		
		assertThat(tested.getClaimId()).isEmpty();
	}
	
	@Test
	void getMultipliers(){
		var multiplier = mock(CommunityPointsMultiplier.class);
		when(channelPointsContextData.getMultipliers()).thenReturn(Optional.of(List.of(multiplier)));
		
		tested.setChannelPointsContext(channelPointsContextData);
		
		assertThat(tested.getActiveMultipliers()).containsExactlyInAnyOrder(multiplier);
	}
	
	@Test
	void getMultipliersEmpty(){
		when(channelPointsContextData.getMultipliers()).thenReturn(Optional.empty());
		
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
}