package fr.raksrinana.channelpointsminer.miner.runnable;

import fr.raksrinana.channelpointsminer.miner.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.miner.factory.StreamerSettingsFactory;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import fr.raksrinana.channelpointsminer.miner.streamer.StreamerSettings;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamerConfigurationReloadUpdateTest{
	private static final String EXISTING_STREAMER_ID = "exits-streamer-id";
	private static final String STREAMER_ID = "streamer-id";
	private static final String STREAMER_USERNAME = "streamer-username";
	
	private StreamerConfigurationReload tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private StreamerSettingsFactory streamerSettingsFactory;
	@Mock
	private GQLApi gqlApi;
	
	@Mock
	private User user;
	@Mock
	private StreamerSettings streamerSettings;
	
	@Mock
	private Streamer existingStreamer;
	
	@BeforeEach
	void setUp(){
		tested = new StreamerConfigurationReload(miner, streamerSettingsFactory, true);
		
		lenient().when(streamerSettingsFactory.getStreamerConfigs()).thenReturn(Stream.empty());
		lenient().when(streamerSettingsFactory.createStreamerSettings(STREAMER_USERNAME)).thenReturn(streamerSettings);
		
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of(existingStreamer));
		
		lenient().when(existingStreamer.getId()).thenReturn(EXISTING_STREAMER_ID);
		lenient().when(existingStreamer.getUsername()).thenReturn(STREAMER_USERNAME);
		
		lenient().when(user.getId()).thenReturn(STREAMER_ID);
		lenient().when(user.getLogin()).thenReturn(STREAMER_USERNAME);
	}
	
	@Test
	void addNew(){
		when(existingStreamer.getUsername()).thenReturn(STREAMER_USERNAME + "Old");
		when(gqlApi.allChannelFollows()).thenReturn(List.of(user));
		
		assertDoesNotThrow(() -> tested.run());
		
		var expectedStreamer = new Streamer(STREAMER_ID, STREAMER_USERNAME, streamerSettings);
		verify(miner).addStreamer(expectedStreamer);
		verify(gqlApi, never()).reportMenuItem(anyString());
	}
	
	@Test
	void updateExisting(){
		when(user.getId()).thenReturn(EXISTING_STREAMER_ID);
		when(gqlApi.allChannelFollows()).thenReturn(List.of(user));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(existingStreamer).setSettings(streamerSettings);
		verify(miner).updateStreamer(existingStreamer);
		verify(gqlApi, never()).reportMenuItem(anyString());
	}
	
	@Test
	void removeOld(){
		when(gqlApi.allChannelFollows()).thenReturn(List.of());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(miner).removeStreamer(existingStreamer);
		verify(gqlApi, never()).reportMenuItem(anyString());
	}
}