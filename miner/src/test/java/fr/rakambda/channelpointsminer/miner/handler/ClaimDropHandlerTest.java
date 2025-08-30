package fr.rakambda.channelpointsminer.miner.handler;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropClaim;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.DropProgress;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.dropclaim.DropClaimData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.dropprogress.DropProgressData;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.DropClaimedChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.impl.DropProgressChannelEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
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
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class ClaimDropHandlerTest{
	private static final String DROP_INSTANCE_ID = "drop-instance-id";
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_USERNAME = "channel-username";
	private static final Instant NOW = Instant.parse("2022-05-15T12:02:14.000Z");
	private static final int CURRENT_PROGRESS = 1;
	private static final int REQUIRED_PROGRESS = 4;
	
	@InjectMocks
	private ClaimDropHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private IEventManager eventManager;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	@Mock
	private DropClaim dropClaim;
	@Mock
	private DropClaimData dropClaimData;
	@Mock
	private DropProgress dropProgress;
	@Mock
	private DropProgressData dropProgressData;
	
	@Mock
	private GQLResponse<DropsPageClaimDropRewardsData> dropsPageClaimDropRewardsDataGQLResponse;
	
	@BeforeEach
	void setUp(){
		lenient().when(dropClaim.getData()).thenReturn(dropClaimData);
		lenient().when(dropClaimData.getChannelId()).thenReturn(CHANNEL_ID);
		lenient().when(dropClaimData.getDropInstanceId()).thenReturn(DROP_INSTANCE_ID);
		lenient().when(dropProgress.getData()).thenReturn(dropProgressData);
		lenient().when(dropProgressData.getChannelId()).thenReturn(CHANNEL_ID);
		lenient().when(dropProgressData.getCurrentProgressMin()).thenReturn(CURRENT_PROGRESS);
		lenient().when(dropProgressData.getRequiredProgressMin()).thenReturn(REQUIRED_PROGRESS);
		
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getStreamerById(CHANNEL_ID)).thenReturn(Optional.of(streamer));
		lenient().when(streamer.getUsername()).thenReturn(CHANNEL_USERNAME);
		
		lenient().when(dropsPageClaimDropRewardsDataGQLResponse.isError()).thenReturn(false);
		lenient().when(gqlApi.dropsPageClaimDropRewards(DROP_INSTANCE_ID)).thenReturn(Optional.of(dropsPageClaimDropRewardsDataGQLResponse));
	}
	
	@Test
	void dropClaim(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			tested.onDropClaim(topic, dropClaim);
			
			verify(eventManager).onEvent(new DropClaimedChannelEvent(CHANNEL_ID, CHANNEL_USERNAME, streamer, NOW));
		}
	}
	
	@Test
	void dropClaimUnknownStreamer(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			lenient().when(miner.getStreamerById(CHANNEL_ID)).thenReturn(Optional.empty());
			
			tested.onDropClaim(topic, dropClaim);
			
			verify(eventManager).onEvent(new DropClaimedChannelEvent(CHANNEL_ID, null, null, NOW));
		}
	}
	
	@Test
	void dropClaimError(){
		when(dropsPageClaimDropRewardsDataGQLResponse.isError()).thenReturn(true);
		
		tested.onDropClaim(topic, dropClaim);
		
		verify(eventManager, never()).onEvent(any(DropClaimedChannelEvent.class));
	}
	
	@Test
	void dropProgress(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			tested.onDropProgress(topic, dropProgress);
			
			verify(eventManager).onEvent(new DropProgressChannelEvent(CHANNEL_ID, CHANNEL_USERNAME, streamer, NOW, 25));
		}
	}
	
	@Test
	void dropProgressUnknownStreamer(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			lenient().when(miner.getStreamerById(CHANNEL_ID)).thenReturn(Optional.empty());
			
			tested.onDropProgress(topic, dropProgress);
			
			verify(eventManager).onEvent(new DropProgressChannelEvent(CHANNEL_ID, null, null, NOW, 25));
		}
	}
}