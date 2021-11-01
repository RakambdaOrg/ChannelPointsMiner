package fr.raksrinana.twitchminer.miner.runnable;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.inventory.InventoryData;
import fr.raksrinana.twitchminer.api.gql.data.types.*;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.MinerData;
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
class SyncInventoryTest{
	private static final String DROP_ID = "drop-id";
	
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
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getMinerData()).thenReturn(minerData);
		lenient().when(response.getData()).thenReturn(inventoryData);
		lenient().when(inventoryData.getCurrentUser()).thenReturn(user);
		lenient().when(user.getInventory()).thenReturn(inventory);
		lenient().when(inventory.getDropCampaignsInProgress()).thenReturn(List.of(dropCampaign));
		lenient().when(dropCampaign.getTimeBasedDrops()).thenReturn(List.of(timeBasedDrop));
		lenient().when(timeBasedDrop.getSelf()).thenReturn(timeBasedDropSelfEdge);
		
		lenient().when(timeBasedDropSelfEdge.isClaimed()).thenReturn(false);
		lenient().when(timeBasedDropSelfEdge.getDropInstanceId()).thenReturn(DROP_ID);
	}
	
	@Test
	void updateInventoryWithDropToClaim(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi).dropsPageClaimDropRewards(DROP_ID);
	}
	
	@Test
	void updateInventoryWithNoDropId(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(timeBasedDropSelfEdge.getDropInstanceId()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
	
	@Test
	void updateInventoryWithAlreadyClaimed(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(timeBasedDropSelfEdge.isClaimed()).thenReturn(true);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
	
	@Test
	void updateInventoryWithDataNoSelfDrop(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(timeBasedDrop.getSelf()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
	
	@Test
	void updateInventoryWithDataNoDrops(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(dropCampaign.getTimeBasedDrops()).thenReturn(List.of());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
	
	@Test
	void updateInventoryWithDataNoCampaignsInProgress(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(inventory.getDropCampaignsInProgress()).thenReturn(List.of());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
	
	@Test
	void updateInventoryWithDataNoInventory(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		when(user.getInventory()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
	
	@Test
	void updateInventoryWithNoData(){
		when(gqlApi.inventory()).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(null);
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
	
	@Test
	void updateInventoryWithException(){
		when(gqlApi.inventory()).thenThrow(new RuntimeException("For tests"));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData, never()).setInventory(any());
		verify(gqlApi, never()).dropsPageClaimDropRewards(any());
	}
}