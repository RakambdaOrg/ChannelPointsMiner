package fr.raksrinana.channelpointsminer.miner.miner;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.inventory.InventoryData;
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
