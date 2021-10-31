package fr.raksrinana.twitchminer.miner.runnable;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.inventory.InventoryData;
import fr.raksrinana.twitchminer.miner.IMiner;
import fr.raksrinana.twitchminer.miner.MinerData;
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
class SyncInventoryTest{
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
	
	@BeforeEach
	void setUp(){
		lenient().when(miner.getGqlApi()).thenReturn(gqlApi);
		lenient().when(miner.getMinerData()).thenReturn(minerData);
		lenient().when(response.getData()).thenReturn(inventoryData);
	}
	
	@Test
	void updateInventoryWithData(){
		when(gqlApi.inventory()).thenReturn(Optional.of(response));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(inventoryData);
	}
	
	@Test
	void updateInventoryWithNoData(){
		when(gqlApi.inventory()).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData).setInventory(null);
	}
	
	@Test
	void updateInventoryWithException(){
		when(gqlApi.inventory()).thenThrow(new RuntimeException("For tests"));
		
		assertDoesNotThrow(() -> tested.run());
		
		verify(minerData, never()).setInventory(any());
	}
}