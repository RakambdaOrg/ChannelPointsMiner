package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem.GetUserIdFromLoginData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import fr.rakambda.channelpointsminer.miner.event.impl.StreamerUnknownEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.StreamerSettingsFactory;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import lombok.SneakyThrows;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class StreamerConfigurationReloadInitialNoFollowsTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-username";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	
	private StreamerConfigurationReload tested;
	
	@TempDir
	private Path tempDir;
	
	@Mock
	private IMiner miner;
	@Mock
	private IEventManager eventManager;
	@Mock
	private StreamerSettingsFactory streamerSettingsFactory;
	@Mock
	private GQLApi gqlApi;
	
	@Mock
	private GetUserIdFromLoginData getUserIdFromLoginData;
	@Mock
	private GQLResponse<GetUserIdFromLoginData> reportMenuItemResponse;
	@Mock
	private User user;
	@Mock
	private StreamerSettings streamerSettings;
	
	@BeforeEach
	void setUp(){
		tested = new StreamerConfigurationReload(miner, eventManager, streamerSettingsFactory, false);
		
		lenient().when(streamerSettingsFactory.getStreamerConfigs()).thenReturn(Stream.empty());
		lenient().when(streamerSettingsFactory.createStreamerSettings(STREAMER_USERNAME)).thenReturn(streamerSettings);
		lenient().when(streamerSettings.isEnabled()).thenReturn(true);
		
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of());
		
		lenient().when(reportMenuItemResponse.getData()).thenReturn(getUserIdFromLoginData);
		lenient().when(getUserIdFromLoginData.getUser()).thenReturn(user);
		lenient().when(user.getId()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void loadFromConfig(){
		when(gqlApi.getUserIdFromLogin(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));
		
		setupStreamerConfig(STREAMER_USERNAME);
		
		assertDoesNotThrow(() -> tested.run());
		
		var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
		
		verify(miner).addStreamer(expectedStreamer);
		verify(gqlApi, never()).allChannelFollows();
		verify(eventManager, never()).onEvent(any());
	}

	@Test
	void loadFromConfigWithDuplicateNames() {
		when(gqlApi.getUserIdFromLogin(STREAMER_USERNAME)).thenReturn(Optional.of(reportMenuItemResponse));

		setupStreamerConfig(STREAMER_USERNAME, STREAMER_USERNAME, STREAMER_USERNAME);

		assertDoesNotThrow(() -> tested.run());

		var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);

		verify(miner).addStreamer(expectedStreamer);
		verify(gqlApi, never()).allChannelFollows();
		verify(eventManager, never()).onEvent(any());
	}
	
	@SneakyThrows
	private void setupStreamerConfig(String... usernames){
		var paths = new ArrayList<Path>();
		for(var username : usernames){
			paths.add(tempDir.resolve(username + ".json"));
		}
		when(streamerSettingsFactory.getStreamerConfigs()).thenReturn(paths.stream());
	}
	
	@Test
	void loadFromConfigUnknown(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(gqlApi.getUserIdFromLogin(STREAMER_USERNAME)).thenReturn(Optional.empty());
			
			setupStreamerConfig(STREAMER_USERNAME);
			
			assertDoesNotThrow(() -> tested.run());
			
			verify(miner, never()).addStreamer(any());
			verify(gqlApi, never()).allChannelFollows();
			verify(eventManager).onEvent(new StreamerUnknownEvent(STREAMER_USERNAME, NOW));
		}
	}
}