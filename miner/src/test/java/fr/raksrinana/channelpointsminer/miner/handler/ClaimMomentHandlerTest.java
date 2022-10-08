package fr.raksrinana.channelpointsminer.miner.handler;

import fr.raksrinana.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.communitymomentcalloutclaim.CommunityMomentCalloutClaimData;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.ClaimCommunityMomentPayload;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.CommunityMomentStart;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.communitymoment.CommunityMomentStartData;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.event.impl.ClaimMomentEvent;
import fr.raksrinana.channelpointsminer.miner.event.impl.ClaimedMomentEvent;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimMomentHandlerTest{
	private static final String MOMENT_ID = "moment-id";
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_USERNAME = "channel-username";
	private static final Instant NOW = Instant.parse("2022-05-15T12:02:14.000Z");
	
	@InjectMocks
	private ClaimMomentHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	
	@Mock
	private CommunityMomentStart communityMomentStart;
	@Mock
	private CommunityMomentStartData communityMomentStartData;
	@Mock
	private GQLResponse<CommunityMomentCalloutClaimData> gqlResponse;
	@Mock
	private CommunityMomentCalloutClaimData communityMomentCalloutClaimData;
	@Mock
	private ClaimCommunityMomentPayload claimCommunityMomentPayload;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getStreamerById(CHANNEL_ID)).thenReturn(Optional.of(streamer));
		lenient().when(streamer.getUsername()).thenReturn(CHANNEL_USERNAME);
		lenient().when(communityMomentStart.getData()).thenReturn(communityMomentStartData);
		lenient().when(communityMomentStartData.getMomentId()).thenReturn(MOMENT_ID);
		lenient().when(communityMomentStartData.getChannelId()).thenReturn(CHANNEL_ID);
		
		lenient().when(gqlResponse.isError()).thenReturn(false);
		lenient().when(gqlResponse.getData()).thenReturn(communityMomentCalloutClaimData);
		lenient().when(communityMomentCalloutClaimData.getMoment()).thenReturn(claimCommunityMomentPayload);
		lenient().when(claimCommunityMomentPayload.getError()).thenReturn(null);
		lenient().when(gqlApi.claimCommunityMoment(MOMENT_ID)).thenReturn(Optional.of(gqlResponse));
	}
	
	@Test
	void onMomentSuccess(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			
			tested.onCommunityMomentStart(topic, communityMomentStart);
			
			verify(gqlApi).claimCommunityMoment(MOMENT_ID);
			verify(miner).onEvent(new ClaimMomentEvent(miner, CHANNEL_ID, CHANNEL_USERNAME, streamer, NOW));
			verify(miner).onEvent(new ClaimedMomentEvent(miner, CHANNEL_ID, CHANNEL_USERNAME, streamer, NOW));
		}
	}
	
	@Test
	void onMomentError(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			
			when(gqlResponse.isError()).thenReturn(true);
			
			tested.onCommunityMomentStart(topic, communityMomentStart);
			
			verify(gqlApi).claimCommunityMoment(MOMENT_ID);
			verify(miner).onEvent(new ClaimMomentEvent(miner, CHANNEL_ID, CHANNEL_USERNAME, streamer, NOW));
			verify(miner, never()).onEvent(any(ClaimedMomentEvent.class));
		}
	}
	
	@Test
	void onMomentEmpty(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			
			when(gqlApi.claimCommunityMoment(MOMENT_ID)).thenReturn(Optional.empty());
			
			tested.onCommunityMomentStart(topic, communityMomentStart);
			
			verify(gqlApi).claimCommunityMoment(MOMENT_ID);
			verify(miner).onEvent(new ClaimMomentEvent(miner, CHANNEL_ID, CHANNEL_USERNAME, streamer, NOW));
			verify(miner, never()).onEvent(any(ClaimedMomentEvent.class));
		}
	}
	
	@Test
	void onMomentDataError(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			
			when(claimCommunityMomentPayload.getError()).thenReturn("error");
			
			tested.onCommunityMomentStart(topic, communityMomentStart);
			
			verify(gqlApi).claimCommunityMoment(MOMENT_ID);
			verify(miner).onEvent(new ClaimMomentEvent(miner, CHANNEL_ID, CHANNEL_USERNAME, streamer, NOW));
			verify(miner, never()).onEvent(any(ClaimedMomentEvent.class));
		}
	}
	
	@Test
	void onMomentUnknownStreamer(){
		try(var factory = mockStatic(TimeFactory.class)){
			factory.when(TimeFactory::now).thenReturn(NOW);
			
			when(miner.getStreamerById(CHANNEL_ID)).thenReturn(Optional.empty());
			
			tested.onCommunityMomentStart(topic, communityMomentStart);
			
			verify(gqlApi).claimCommunityMoment(MOMENT_ID);
			verify(miner).onEvent(new ClaimMomentEvent(miner, CHANNEL_ID, null, null, NOW));
		}
	}
}