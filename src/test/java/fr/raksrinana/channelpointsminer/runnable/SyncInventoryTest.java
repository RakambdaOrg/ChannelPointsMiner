package fr.raksrinana.channelpointsminer.runnable;

import fr.raksrinana.channelpointsminer.api.gql.GQLApi;
import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.inventory.InventoryData;
import fr.raksrinana.channelpointsminer.api.gql.data.types.DropCampaign;
import fr.raksrinana.channelpointsminer.api.gql.data.types.Inventory;
import fr.raksrinana.channelpointsminer.api.gql.data.types.TimeBasedDrop;
import fr.raksrinana.channelpointsminer.api.gql.data.types.TimeBasedDropSelfEdge;
import fr.raksrinana.channelpointsminer.api.gql.data.types.User;
import fr.raksrinana.channelpointsminer.event.impl.DropClaimEvent;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.MinerData;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncInventoryTest{
	private static final String DROP_ID = "drop-id";
	private static final Instant NOW = Instant.parse("2020-05-17T12:14:20.000Z");
	
	@InjectMocks
	private SyncInventory tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private MinerData minerData;
	@Mock
	private GQLApi gqlApi;
	@Mock
	private GQLResponse<InventoryData> response;
	@Mock
	private InventoryData inventoryData;
	@Mock
	private User user;
	@Mock
	private Inventory inventory;
	@Mock
	private DropCampaign dropCampaign;
	@Mock
	private TimeBasedDrop timeBasedDrop;
	@Mock
	private TimeBasedDropSelfEdge timeBasedDropSelfEdge;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getStreamers()).thenReturn(List.of(streamer));
		lenient().when(miner.getMinerData()).thenReturn(minerData);
		
		lenient().when(response.getData()).thenReturn(inventoryData);
		lenient().when(inventoryData.getCurrentUser()).thenReturn(user);
		lenient().when(user.getInventory()).thenReturn(inventory);
		lenient().when(inventory.getDropCampaignsInProgress()).thenReturn(List.of(dropCampaign));
		lenient().when(dropCampaign.getTimeBasedDrops()).thenReturn(List.of(timeBasedDrop));
		lenient().when(timeBasedDrop.getSelf()).thenReturn(timeBasedDropSelfEdge);
		
		lenient().when(streamer.isParticipateCampaigns()).thenReturn(true);
		
		lenient().when(timeBasedDropSelfEdge.isClaimed()).thenReturn(false);
		lenient().when(timeBasedDropSelfEdge.getDropInstanceId()).thenReturn(DROP_ID);
	}
	
	@Test
	void updateInventoryWithDropToClaim(){
		try(var timeFactory = mockStatic(TimeFactory.class)){
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(gqlApi.inventory()).thenReturn(Optional.of(response));
			assertDoesNotThrow(() -> tested.run());
			
			verify(minerData).setInventory(inventoryData);
			verify(gqlApi).dropsPageClaimDropRewards(DROP_ID);
			verify(miner).onEvent(new DropClaimEvent(miner, timeBasedDrop, NOW));
		}
	}
	
	@Test
	void updateInventoryWithNoDropId(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(timeBasedDropSelfEdge.getDropInstanceId()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryWithAlreadyClaimed(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(timeBasedDropSelfEdge.isClaimed()).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryWithDataNoSelfDrop(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(timeBasedDrop.getSelf()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryWithDataNoDrops(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(dropCampaign.getTimeBasedDrops()).thenReturn(List.of());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryWithDataNoCampaignsInProgress(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(inventory.getDropCampaignsInProgress()).thenReturn(List.of());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryWithDataNoInventory(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(user.getInventory()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryWithNoData(){
		when(gqlApi.inventory()).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(null);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryWithException(){
		when(gqlApi.inventory()).thenThrow(new RuntimeException("For tests"));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData, never()).setInventory(any());
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryNoStreamers(){
		when(miner.getStreamers()).thenReturn(List.of());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData, never()).setInventory(any());
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
	
	@Test
	void updateInventoryNoStreamersInCampaigns(){
		when(streamer.isParticipateCampaigns()).thenReturn(false);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData, never()).setInventory(any());
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
		verify(miner, never()).onEvent(any());
	}
}