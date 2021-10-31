package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.gql.data.inventory.InventoryData;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class MinerData{
	@Getter
	@Setter
	@Nullable
	private InventoryData inventory;
}
