package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.inventory;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class InventoryOperation extends IGQLOperation<InventoryData>{
    public InventoryOperation(){
        super("Inventory");
        addPersistedQueryExtension(new PersistedQueryExtension(1, "3ab317a5753b25125f47d4ce962ebe928ff4e85047b77508340c94ebc20b6230"));
        addVariable("fetchRewardCampaigns", true);
    }
    
    @Override
    @NonNull
    public GenericType<GQLResponse<InventoryData>> getResponseType(){
        return new GenericType<>(){};
    }
}
