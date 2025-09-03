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
        addPersistedQueryExtension(new PersistedQueryExtension(1, "37fea486d6179047c41d0f549088a4c3a7dd60c05c70956a1490262f532dccd9"));
        addVariable("fetchRewardCampaigns", true);
    }
    
    @Override
    @NonNull
    public GenericType<GQLResponse<InventoryData>> getResponseType(){
        return new GenericType<>(){};
    }
}
