package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.ws.data.message.ClaimAvailable;
import fr.raksrinana.channelpointsminer.api.ws.data.message.claimavailable.ClaimAvailableData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Claim;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimAvailableHandlerTest{
	private static final String CLAIM_ID = "claim-id";
	private static final String CHANNEL_ID = "channel-id";
	
	@InjectMocks
	private ClaimAvailableHandler tested;
	
	@Mock
	private IMiner miner;
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
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
	}
	
	@Test
	void claim(){
		when(claimAvailable.getData()).thenReturn(claimAvailableData);
		when(claimAvailableData.getClaim()).thenReturn(claim);
		when(claim.getId()).thenReturn(CLAIM_ID);
		when(claim.getChannelId()).thenReturn(CHANNEL_ID);
		
		assertDoesNotThrow(() -> tested.handle(topic, claimAvailable));
		
		verify(gqlApi).claimCommunityPoints(CHANNEL_ID, CLAIM_ID);
	}
}