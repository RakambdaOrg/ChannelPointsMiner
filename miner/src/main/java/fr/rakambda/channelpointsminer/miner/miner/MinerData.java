package fr.rakambda.channelpointsminer.miner.miner;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory.InventoryData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class MinerData{
	@Nullable
	private InventoryData inventory;
}
