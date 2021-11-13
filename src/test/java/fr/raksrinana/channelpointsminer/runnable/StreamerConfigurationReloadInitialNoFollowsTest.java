package fr.raksrinana.channelpointsminer.runnable;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.api.kraken.KrakenApi;
import fr.raksrinana.channelpointsminer.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import lombok.SneakyThrows;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamerConfigurationReloadInitialNoFollowsTest{
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
		tested = new StreamerConfigurationReload(miner, streamerSettingsFactory, krakenApi, false);
		
		lenient().when(streamerSettingsFactory.getStreamerConfigs()).thenReturn(Stream.empty());
		lenient().when(streamerSettingsFactory.createStreamerSettings(STREAMER_USERNAME)).thenReturn(streamerSettings);
		
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		
		lenient().when(reportMenuItemResponse.getData()).thenReturn(reportMenuItemData);
		lenient().when(reportMenuItemData.getUser()).thenReturn(user);
		lenient().when(user.getId()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void loadFromConfig(){
		when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
		
		setupStreamerConfig(STREAMER_USERNAME);
		
		assertDoesNotThrow(() -> tested.run());
		
		var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
		
		verify(miner).addStreamer(expectedStreamer);
		verify(krakenApi, never()).getFollows();
	}
	
	@SneakyThrows
	private void setupStreamerConfig(String... usernames){
		var paths = new ArrayList<Path>();
		for(var username : usernames){
			paths.add(Files.writeString(tempDir.resolve(username + ".json"), "{}", TRUNCATE_EXISTING, CREATE));
		}
		when(streamerSettingsFactory.getStreamerConfigs()).thenReturn(paths.stream());
	}
	
	@Test
	void loadFromConfigUnknown(){
		when(gqlApi.reportMenuItem(STREAMER_USERNAME)).thenReturn(Optional.empty());
		
		setupStreamerConfig(STREAMER_USERNAME);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(miner, never()).addStreamer(any());
		verify(krakenApi, never()).getFollows();
	}
}