package fr.raksrinana.twitchminer.miner.runnables;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.channelpointscontext.ChannelPointsContextData;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.data.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateChannelPointsContextTest{
	private static final String STREAMER_USERNAME = "streamer-username";
	
	@InjectMocks
	private UpdateChannelPointsContext tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of(streamer));
		
		lenient().when(streamer.getUsername()).thenReturn(STREAMER_USERNAME);
	}
	
	@Test
	void updateWithData(){
		var data = ChannelPointsContextData.builder().build();
		var response = GQLResponse.<ChannelPointsContextData> builder()
				.data(data)
				.build();
		
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.of(response));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).channelPointsContext(STREAMER_USERNAME);
		verify(streamer).setChannelPointsContext(data);
	}
	
	@Test
	void updateNoData(){
		when(gqlApi.channelPointsContext(STREAMER_USERNAME)).thenReturn(Optional.empty());
		when(miner.getStreamers()).thenReturn(List.of(streamer));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi).channelPointsContext(anyString());
		verify(streamer).setChannelPointsContext(null);
	}
	
	@Test
	void updateSeveral(){
		when(miner.getStreamers()).thenReturn(List.of(streamer, streamer));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(gqlApi, times(2)).channelPointsContext(anyString());
	}
	
	@Test
	void updateException(){
		when(gqlApi.channelPointsContext(any())).thenThrow(new RuntimeException("For tests"));
		
		assertDoesNotThrow(() -> tested.run());
	}
}