package fr.raksrinana.channelpointsminer.runnable;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.api.kraken.KrakenApi;
import fr.raksrinana.channelpointsminer.api.kraken.data.follows.Channel;
import fr.raksrinana.channelpointsminer.api.kraken.data.follows.Follow;
import fr.raksrinana.channelpointsminer.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamerConfigurationReloadInitialWithFollowsTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-username";
	
	private StreamerConfigurationReload tested;
	
	@TempDir
	private Path tempDir;
	
	@Mock
	private IMiner miner;
	@Mock
	private StreamerSettingsFactory streamerSettingsFactory;
	@Mock
	private KrakenApi krakenApi;
	@Mock
	private GQLApi gqlApi;
	
	@Mock
	private ReportMenuItemData reportMenuItemData;
	@Mock
	private GQLResponse<ReportMenuItemData> reportMenuItemResponse;
	@Mock
	private User user;
	@Mock
	private StreamerSettings streamerSettings;
	
	@BeforeEach
	void setUp(){
		tested = new StreamerConfigurationReload(miner, streamerSettingsFactory, krakenApi, true);
		
		lenient().when(streamerSettingsFactory.getStreamerConfigs()).thenReturn(Stream.empty());
		lenient().when(streamerSettingsFactory.createStreamerSettings(STREAMER_USERNAME)).thenReturn(streamerSettings);
		
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		
		lenient().when(reportMenuItemResponse.getData()).thenReturn(reportMenuItemData);
		lenient().when(reportMenuItemData.getUser()).thenReturn(user);
		lenient().when(user.getId()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void loadFromFollows(){
		var channel = mock(Channel.class);
		var follow = mock(Follow.class);
		when(follow.getChannel()).thenReturn(channel);
		when(channel.getId()).thenReturn(STREAMER_ID);
		when(channel.getName()).thenReturn(STREAMER_USERNAME);
		
		when(krakenApi.getFollows()).thenReturn(List.of(follow));
		
		assertDoesNotThrow(() -> tested.run());
		
		var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
		verify(miner).addStreamer(expectedStreamer);
		verify(gqlApi, never()).reportMenuItem(anyString());
	}
	
	@Test
	void loadFromFollowsEmpty(){
		when(krakenApi.getFollows()).thenReturn(List.of());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(miner, never()).addStreamer(any());
		verify(gqlApi, never()).reportMenuItem(anyString());
	}
}