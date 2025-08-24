package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.ClaimAvailable;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.claimavailable.ClaimAvailableData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Claim;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.ClaimAvailableEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class ClaimAvailableHandlerTest{
	private static final String CLAIM_ID = "claim-id";
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_NAME = "channel-name";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	private static final ZonedDateTime ZONED_NOW = ZonedDateTime.ofInstant(NOW, ZoneId.systemDefault());
	
	@InjectMocks
	private ClaimAvailableHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private IEventManager eventManager;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private ClaimAvailable claimAvailable;
	@Mock
	private ClaimAvailableData claimAvailableData;
	@Mock
	private Claim claim;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getStreamerById(CHANNEL_ID)).thenReturn(Optional.of(streamer));
		lenient().when(streamer.getUsername()).thenReturn(CHANNEL_NAME);
	}
	
	@Test
	void claim(){
		when(claimAvailable.getData()).thenReturn(claimAvailableData);
		when(claimAvailableData.getTimestamp()).thenReturn(ZonedDateTime.ofInstant(NOW, ZoneId.systemDefault()));
		when(claimAvailableData.getClaim()).thenReturn(claim);
		when(claim.getId()).thenReturn(CLAIM_ID);
		when(claim.getChannelId()).thenReturn(CHANNEL_ID);
		
		assertDoesNotThrow(() -> tested.handle(topic, claimAvailable));
		
		verify(gqlApi).claimCommunityPoints(CHANNEL_ID, CLAIM_ID);
		verify(eventManager).onEvent(new ClaimAvailableEvent(CHANNEL_ID, CHANNEL_NAME, streamer, NOW));
	}
	
	@Test
	void claimUnknownStreamer(){
		when(miner.getStreamerById(CHANNEL_ID)).thenReturn(Optional.empty());
		
		when(claimAvailable.getData()).thenReturn(claimAvailableData);
		when(claimAvailableData.getTimestamp()).thenReturn(ZONED_NOW);
		when(claimAvailableData.getClaim()).thenReturn(claim);
		when(claim.getId()).thenReturn(CLAIM_ID);
		when(claim.getChannelId()).thenReturn(CHANNEL_ID);
		
		assertDoesNotThrow(() -> tested.handle(topic, claimAvailable));
		
		verify(gqlApi).claimCommunityPoints(CHANNEL_ID, CLAIM_ID);
		verify(eventManager).onEvent(new ClaimAvailableEvent(CHANNEL_ID, null, null, NOW));
	}
}