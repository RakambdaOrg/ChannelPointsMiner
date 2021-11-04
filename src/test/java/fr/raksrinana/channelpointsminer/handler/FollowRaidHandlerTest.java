package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.ws.data.message.RaidUpdateV2;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Raid;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowRaidHandlerTest{
	private static final String RAID_ID = "raid-id";
	private static final String STREAMER_ID = "streamer-id";
	
	@InjectMocks
	private FollowRaidHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private RaidUpdateV2 raidUpdateV2;
	@Mock
	private Raid raid;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(raidUpdateV2.getRaid()).thenReturn(raid);
		lenient().when(raid.getId()).thenReturn(RAID_ID);
	}
	
	@Test
	void claim(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		when(streamer.followRaids()).thenReturn(true);
		
		when(raidUpdateV2.getRaid()).thenReturn(raid);
		when(raid.getId()).thenReturn(RAID_ID);
		
		assertDoesNotThrow(() -> tested.handle(topic, raidUpdateV2));
		
		verify(gqlApi).joinRaid(RAID_ID);
	}
	
	@Test
	void claimUnknownStreamer(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.handle(topic, raidUpdateV2));
		
		verify(gqlApi, never()).joinRaid(any());
	}
	
	@Test
	void claimStreamerDoesNotFollowRaids(){
		when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		when(streamer.followRaids()).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.handle(topic, raidUpdateV2));
		
		verify(gqlApi, never()).joinRaid(any());
	}
}