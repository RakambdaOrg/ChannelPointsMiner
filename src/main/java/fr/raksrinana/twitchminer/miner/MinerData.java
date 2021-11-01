package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.gql.data.inventory.InventoryData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class MinerData{
	@Nullable
	private InventoryData inventory;
}
