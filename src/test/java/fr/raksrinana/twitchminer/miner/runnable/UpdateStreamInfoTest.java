package fr.raksrinana.twitchminer.miner.runnable;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.api.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.raksrinana.twitchminer.api.twitch.TwitchApi;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStreamInfoTest{
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final String STREAMER_ID = "streamer-id";
	
	@InjectMocks
	private UpdateStreamInfo tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private TwitchApi twitchApi;
	@Mock
	private Streamer streamer;
	@Mock
	private User user;
	@Mock
	private GQLResponse<VideoPlayerStreamInfoOverlayChannelData> gqlResponseVideoPlayer;
	@Mock
	private GQLResponse<ChannelPointsContextData> gqlResponseChannelPoints;
	@Mock
	private VideoPlayerStreamInfoOverlayChannelData videoPlayerStreamInfoOverlayChannelData;
	@Mock
	private ChannelPointsContextData channelPointsContextData;
	
	private URL spadeUrl;
	private URL streamerUrl;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		spadeUrl = new URL("https://google.com/");
		streamerUrl = new URL("https://google.com/streamer");
		
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getTwitchApi()).thenReturn(twitchApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of(streamer));
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		lenient().when(streamer.getChannelUrl()).thenReturn(streamerUrl);
		lenient().when(streamer.getClaimId()).thenReturn(Optional.empty());
		lenient().when(streamer.needUpdate()).thenReturn(true);
		
		lenient().when(gqlResponseVideoPlayer.getData()).thenReturn(videoPlayerStreamInfoOverlayChannelData);
		lenient().when(gqlResponseChannelPoints.getData()).thenReturn(channelPointsContextData);
		lenient().when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
	}
	
	@Test
	void updateWithDataNotStreaming(){
		when(streamer.isStreaming()).thenReturn(false);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer).setSpadeUrl(null);
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateWithNoDataNotStreaming(){
		when(streamer.isStreaming()).thenReturn(false);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.empty());
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(null);
		verify(streamer).setChannelPointsContext(null);
		verify(streamer).setSpadeUrl(null);
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateWithDataStreamingAndSpadeUrlPresent(){
		when(streamer.isStreaming()).thenReturn(true);
		when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer, never()).setSpadeUrl(any());
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateWithDataStreamingAndSpadeUrlMissing(){
		when(streamer.isStreaming()).thenReturn(true);
		when(streamer.getSpadeUrl()).thenReturn(null);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		when(twitchApi.getSpadeUrl(streamerUrl)).thenReturn(Optional.of(spadeUrl));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi).getSpadeUrl(streamerUrl);
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer).setSpadeUrl(spadeUrl);
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateWithDataStreamingAndSpadeUrlMissingAndNotReturned(){
		when(streamer.isStreaming()).thenReturn(true);
		when(streamer.getSpadeUrl()).thenReturn(null);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		when(twitchApi.getSpadeUrl(streamerUrl)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi).getSpadeUrl(streamerUrl);
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer, never()).setSpadeUrl(any());
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateWithDataStreamingUpdateCampaign(){
		var data = mock(DropsHighlightServiceAvailableDropsData.class);
		var response = (GQLResponse<DropsHighlightServiceAvailableDropsData>) mock(GQLResponse.class);
		when(response.getData()).thenReturn(data);
		
		when(streamer.isStreaming()).thenReturn(true);
		when(streamer.updateCampaigns()).thenReturn(true);
		when(streamer.isStreamingGame()).thenReturn(true);
		when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		when(gqlApi.dropsHighlightServiceAvailableDrops(STREAMER_ID)).thenReturn(Optional.of(response));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi).dropsHighlightServiceAvailableDrops(STREAMER_ID);
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer, never()).setSpadeUrl(any());
		verify(streamer).setDropsHighlightServiceAvailableDrops(data);
	}
	
	@Test
	void updateWithDataStreamingUpdateCampaignNoResponse(){
		when(streamer.isStreaming()).thenReturn(true);
		when(streamer.updateCampaigns()).thenReturn(true);
		when(streamer.isStreamingGame()).thenReturn(true);
		when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		when(gqlApi.dropsHighlightServiceAvailableDrops(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi).dropsHighlightServiceAvailableDrops(STREAMER_ID);
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer, never()).setSpadeUrl(any());
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateWithDataStreamingUpdateCampaignNotStreamingGame(){
		when(streamer.isStreaming()).thenReturn(true);
		when(streamer.updateCampaigns()).thenReturn(true);
		when(streamer.isStreamingGame()).thenReturn(false);
		when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer, never()).setSpadeUrl(any());
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateSeveral(){
		when(streamer.isStreaming()).thenReturn(false);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.empty());
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.empty());
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi, times(2)).videoPlayerStreamInfoOverlayChannel(anyString());
		verify(gqlApi, times(2)).channelPointsContext(anyString());
		
		verify(streamer, times(2)).setVideoPlayerStreamInfoOverlayChannel(null);
		verify(streamer, times(2)).setChannelPointsContext(null);
		verify(streamer, times(2)).setSpadeUrl(null);
		verify(streamer, times(2)).setDropsHighlightServiceAvailableDrops(null);
	}
	
	@Test
	void updateException(){
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(any())).thenThrow(new RuntimeException("For tests"));
		
		assertDoesNotThrow(() -> tested.run());
	}
	
	@Test
	void notUpdatingIfNotNeeded(){
		when(streamer.needUpdate()).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi, never()).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi, never()).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer, never()).setVideoPlayerStreamInfoOverlayChannel(any());
		verify(streamer, never()).setChannelPointsContext(any());
		verify(streamer, never()).setSpadeUrl(any());
		verify(streamer, never()).setDropsHighlightServiceAvailableDrops(any());
	}
	
	@Test
	void updatingIfNotNeededByManuallyLaunched(){
		lenient().when(streamer.needUpdate()).thenReturn(false);
		when(streamer.isStreaming()).thenReturn(false);
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
		
		assertDoesNotThrow(() -> tested.run(streamer));
		
		verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
		verify(twitchApi, never()).getSpadeUrl(any(URL.class));
		
		verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
		verify(streamer).setChannelPointsContext(channelPointsContextData);
		verify(streamer).setSpadeUrl(null);
		verify(streamer).setDropsHighlightServiceAvailableDrops(null);
	}
}