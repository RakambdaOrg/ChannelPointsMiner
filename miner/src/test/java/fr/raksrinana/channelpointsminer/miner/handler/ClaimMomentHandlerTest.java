package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.CommunityMomentStart;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.communitymoment.CommunityMomentStartData;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClaimMomentHandlerTest{
	private static final String MOMENT_ID = "moment-id";
	
	@InjectMocks
	private ClaimMomentHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private Topic topic;
	
	@Mock
	private CommunityMomentStart communityMomentStart;
	@Mock
	private CommunityMomentStartData communityMomentStartData;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(communityMomentStart.getData()).thenReturn(communityMomentStartData);
		lenient().when(communityMomentStartData.getMomentId()).thenReturn(MOMENT_ID);
	}
	
	@Test
	void onMoment(){
		tested.onCommunityMomentStart(topic, communityMomentStart);
		
		verify(gqlApi).claimCommunityMoment(MOMENT_ID);
	}
}