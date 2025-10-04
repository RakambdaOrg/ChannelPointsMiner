package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus.ChatRoomBanStatusData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.playbackaccesstoken.PlaybackAccessTokenData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.Channel;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ChatRoomBanStatus;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropCampaignSummary;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.StreamPlaybackAccessToken;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.videoplayerstreaminfooverlaychannel.VideoPlayerStreamInfoOverlayChannelData;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Disabled;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class UpdateStreamInfoTest{
	private static final Instant NOW = Instant.parse("2021-10-25T14:26:32Z");
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final String STREAMER_ID = "streamer-id";
	private static final String ACCOUNT_ID = "account-id";
	private static final String M3U8_SIGNATURE = "signature";
	private static final String M3U8_VALUE = "value";
	private static final String CAMPAIGN_ID = "campaign-id";
	
	@InjectMocks
	private UpdateStreamInfo tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private TwitchApi twitchApi;
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private Streamer streamer;
	@Mock
	private User user;
	@Mock
	private GQLResponse<VideoPlayerStreamInfoOverlayChannelData> gqlResponseVideoPlayer;
	@Mock
	private GQLResponse<ChannelPointsContextData> gqlResponseChannelPoints;
	@Mock
	private GQLResponse<DropsHighlightServiceAvailableDropsData> dropsHighlightServiceAvailableDrops;
	@Mock
	private GQLResponse<ChatRoomBanStatusData> gqlResponseChatRoomBanStatus;
	@Mock
	private GQLResponse<PlaybackAccessTokenData> gqlResponsePlaybackAccessToken;
	@Mock
	private VideoPlayerStreamInfoOverlayChannelData videoPlayerStreamInfoOverlayChannelData;
	@Mock
	private ChannelPointsContextData channelPointsContextData;
	@Mock
	private ChatRoomBanStatusData chatRoomBanStatusData;
	@Mock
	private PlaybackAccessTokenData playbackAccessTokenData;
	@Mock
	private DropsHighlightServiceAvailableDropsData dropsHighlightServiceAvailableDropsData;
	@Mock
	private Channel channel;
	@Mock
	private DropCampaign dropCampaign;
	@Mock
	private DropCampaignSummary dropCampaignSummary;
	
	private URL spadeUrl;
	private URL m3u8Url;
	private URL streamerUrl;
	
	@BeforeEach
	void setUp() throws MalformedURLException{
		spadeUrl = URI.create("https://google.com/").toURL();
		m3u8Url = URI.create("https://google.m3u8/").toURL();
		streamerUrl = URI.create("https://google.com/streamer").toURL();
		
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getTwitchApi()).thenReturn(twitchApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of(streamer));
		lenient().when(miner.getTwitchLogin()).thenReturn(twitchLogin);
		
		lenient().when(twitchLogin.fetchUserId(gqlApi)).thenReturn(ACCOUNT_ID);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
		lenient().when(streamer.getChannelUrl()).thenReturn(streamerUrl);
		lenient().when(streamer.isParticipateCampaigns()).thenReturn(true);
		lenient().when(streamer.getClaimId()).thenReturn(Optional.empty());
		lenient().when(streamer.needUpdate()).thenReturn(true);
		
		lenient().when(gqlResponseVideoPlayer.getData()).thenReturn(videoPlayerStreamInfoOverlayChannelData);
		lenient().when(gqlResponseChannelPoints.getData()).thenReturn(channelPointsContextData);
		lenient().when(videoPlayerStreamInfoOverlayChannelData.getUser()).thenReturn(user);
		lenient().when(gqlResponseChatRoomBanStatus.getData()).thenReturn(chatRoomBanStatusData);
		lenient().when(gqlResponsePlaybackAccessToken.getData()).thenReturn(playbackAccessTokenData);
		
		lenient().when(dropsHighlightServiceAvailableDrops.getData()).thenReturn(dropsHighlightServiceAvailableDropsData);
		lenient().when(dropsHighlightServiceAvailableDropsData.getChannel()).thenReturn(channel);
		lenient().when(channel.getViewerDropCampaigns()).thenReturn(List.of(dropCampaign));
		lenient().when(dropCampaign.getId()).thenReturn(CAMPAIGN_ID);
	}
	
	@Test
	void updateWithDataNotStreaming(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(false);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(gqlApi, never()).chatRoomBanStatus(anyString(), anyString());
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer).setSpadeUrl(null);
			// verify(streamer).setM3u8Url(null);
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer, never()).setChatBanned(anyBoolean());
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataNotStreamingButWasBefore(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.hasStreamedEnoughTime()).thenReturn(true);
			when(streamer.isStreaming()).thenReturn(true).thenReturn(false);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.empty());
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(gqlApi, never()).chatRoomBanStatus(anyString(), anyString());
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any());
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(null);
			verify(streamer).setChannelPointsContext(null);
			verify(streamer).setSpadeUrl(null);
			// verify(streamer).setM3u8Url(null);
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setLastOffline(NOW);
			verify(streamer).resetWatchedDuration();
			verify(streamer, never()).setChatBanned(anyBoolean());
		}
	}
	
	@Test
	void updateWithNoDataNotStreaming(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(false);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.empty());
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(gqlApi, never()).chatRoomBanStatus(anyString(), anyString());
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(null);
			verify(streamer).setChannelPointsContext(null);
			verify(streamer).setSpadeUrl(null);
			// verify(streamer).setM3u8Url(null);
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer, never()).setChatBanned(anyBoolean());
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingAndSpadeAndM3u8UrlPresent(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
			// when(streamer.getM3u8Url()).thenReturn(m3u8Url);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(any());
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingAndChatBanned(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
			// when(streamer.getM3u8Url()).thenReturn(m3u8Url);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			
			when(chatRoomBanStatusData.getChatRoomBanStatus()).thenReturn(mock(ChatRoomBanStatus.class));
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(any());
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(true);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	@Disabled("M3U8 disabled")
	void updateWithDataStreamingAndSpadeAndM3u8UrlMissing(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.getSpadeUrl()).thenReturn(null);
			when(streamer.getM3u8Url()).thenReturn(null);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			when(gqlApi.playbackAccessToken(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponsePlaybackAccessToken));
			when(twitchApi.getSpadeUrl(streamerUrl)).thenReturn(Optional.of(spadeUrl));
			when(twitchApi.getM3u8Url(STREAMER_USERNAME, M3U8_SIGNATURE, M3U8_VALUE)).thenReturn(Optional.of(m3u8Url));
			
			var streamPlaybackAccessToken = mock(StreamPlaybackAccessToken.class);
			when(playbackAccessTokenData.getStreamPlaybackAccessToken()).thenReturn(streamPlaybackAccessToken);
			when(streamPlaybackAccessToken.getSignature()).thenReturn(M3U8_SIGNATURE);
			when(streamPlaybackAccessToken.getValue()).thenReturn(M3U8_VALUE);
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(twitchApi).getSpadeUrl(streamerUrl);
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer).setSpadeUrl(spadeUrl);
			verify(streamer).setM3u8Url(m3u8Url);
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingAndSpadeAndM3u8UrlMissingButDropsNotActivated(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.isParticipateCampaigns()).thenReturn(false);
			when(streamer.getSpadeUrl()).thenReturn(null);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			when(twitchApi.getSpadeUrl(streamerUrl)).thenReturn(Optional.of(spadeUrl));
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(twitchApi).getSpadeUrl(streamerUrl);
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer).setSpadeUrl(spadeUrl);
			// verify(streamer).setM3u8Url(null);
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingAndSpadeUrlMissingAndNotReturned(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.getSpadeUrl()).thenReturn(null);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			when(twitchApi.getSpadeUrl(streamerUrl)).thenReturn(Optional.empty());
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(twitchApi).getSpadeUrl(streamerUrl);
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(null);
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingUpdateCampaign(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.isParticipateCampaigns()).thenReturn(true);
			when(streamer.isStreamingGame()).thenReturn(true);
			when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
			// when(streamer.getM3u8Url()).thenReturn(m3u8Url);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.dropsHighlightServiceAvailableDrops(STREAMER_ID)).thenReturn(Optional.of(dropsHighlightServiceAvailableDrops));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi).dropsHighlightServiceAvailableDrops(STREAMER_ID);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(any());
			verify(streamer).setDropsHighlightServiceAvailableDrops(dropsHighlightServiceAvailableDropsData);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingUpdateCampaignDismissibleAndSettingActivated(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			var dropId = "dc4ff0b4-4de0-11ef-9ec3-621fb0811846";
			when(dropCampaign.getId()).thenReturn(dropId);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.isParticipateCampaigns()).thenReturn(true);
			when(streamer.isDismissKnownGlobalCampaigns()).thenReturn(true);
			when(streamer.isStreamingGame()).thenReturn(true);
			when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
			// when(streamer.getM3u8Url()).thenReturn(m3u8Url);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.dropsHighlightServiceAvailableDrops(STREAMER_ID)).thenReturn(Optional.of(dropsHighlightServiceAvailableDrops));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			
			when(dropCampaign.getSummary()).thenReturn(dropCampaignSummary);
			when(dropCampaignSummary.isSitewide()).thenReturn(true);
			when(dropCampaignSummary.isPermanentlyDismissible()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi).dropsHighlightServiceAvailableDrops(STREAMER_ID);
			verify(gqlApi).setDropsCommunityHighlightToHidden(STREAMER_ID, dropId);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(any());
			verify(streamer).setDropsHighlightServiceAvailableDrops(dropsHighlightServiceAvailableDropsData);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingUpdateCampaignNoResponse(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.isParticipateCampaigns()).thenReturn(true);
			when(streamer.isStreamingGame()).thenReturn(true);
			when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
			// when(streamer.getM3u8Url()).thenReturn(m3u8Url);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.dropsHighlightServiceAvailableDrops(STREAMER_ID)).thenReturn(Optional.empty());
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi).dropsHighlightServiceAvailableDrops(STREAMER_ID);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(any());
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateWithDataStreamingUpdateCampaignNotStreamingGame(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(streamer.isStreaming()).thenReturn(true);
			when(streamer.isParticipateCampaigns()).thenReturn(true);
			when(streamer.isStreamingGame()).thenReturn(false);
			when(streamer.getSpadeUrl()).thenReturn(spadeUrl);
			// when(streamer.getM3u8Url()).thenReturn(m3u8Url);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			when(gqlApi.chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID)).thenReturn(Optional.of(gqlResponseChatRoomBanStatus));
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi).chatRoomBanStatus(STREAMER_ID, ACCOUNT_ID);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(gqlApi, never()).playbackAccessToken(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(any());
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateSeveral(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
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
			// verify(streamer, times(2)).setM3u8Url(null);
			verify(streamer, times(2)).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer, times(2)).setLastUpdated(NOW);
			verify(streamer, never()).setChatBanned(false);
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
	
	@Test
	void updateException(){
		when(gqlApi.videoPlayerStreamInfoOverlayChannel(any())).thenThrow(new RuntimeException("For tests"));
		
		assertDoesNotThrow(() -> tested.run());
	}
	
	@Test
	void notUpdatingIfNotNeeded(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			when(streamer.needUpdate()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(gqlApi, never()).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi, never()).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(gqlApi, never()).chatRoomBanStatus(anyString(), anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer, never()).setVideoPlayerStreamInfoOverlayChannel(any());
			verify(streamer, never()).setChannelPointsContext(any());
			verify(streamer, never()).setSpadeUrl(any());
			// verify(streamer, never()).setM3u8Url(any());
			verify(streamer, never()).setDropsHighlightServiceAvailableDrops(any());
			verify(streamer, never()).setLastUpdated(any());
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
			verify(streamer, never()).setChatBanned(anyBoolean());
		}
	}
	
	@Test
	void updatingIfNotNeededByManuallyLaunched(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			lenient().when(streamer.needUpdate()).thenReturn(false);
			when(streamer.isStreaming()).thenReturn(false);
			when(gqlApi.videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseVideoPlayer));
			when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(gqlResponseChannelPoints));
			
			assertDoesNotThrow(() -> tested.run(streamer));
			
			verify(gqlApi).videoPlayerStreamInfoOverlayChannel(STREAMER_USERNAME);
			verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
			verify(gqlApi, never()).chatRoomBanStatus(anyString(), anyString());
			verify(gqlApi, never()).dropsHighlightServiceAvailableDrops(anyString());
			verify(twitchApi, never()).getSpadeUrl(any(URL.class));
			
			verify(streamer).setVideoPlayerStreamInfoOverlayChannel(videoPlayerStreamInfoOverlayChannelData);
			verify(streamer).setChannelPointsContext(channelPointsContextData);
			verify(streamer).setSpadeUrl(null);
			// verify(streamer).setM3u8Url(null);
			verify(streamer).setDropsHighlightServiceAvailableDrops(null);
			verify(streamer).setLastUpdated(NOW);
			verify(streamer, never()).setChatBanned(anyBoolean());
			verify(streamer, never()).setLastOffline(any());
			verify(streamer, never()).resetWatchedDuration();
		}
	}
}