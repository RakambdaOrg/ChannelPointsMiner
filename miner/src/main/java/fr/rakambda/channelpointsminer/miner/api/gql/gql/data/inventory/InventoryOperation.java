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
        addPersistedQueryExtension(new PersistedQueryExtension(1, "d86775d0ef16a63a33ad52e80eaff963b2d5b72fada7c991504a57496e1d8e4b"));
        addVariable("fetchRewardCampaigns", true);
    }
    
    @Override
    @NonNull
    public GenericType<GQLResponse<InventoryData>> getResponseType(){
        return new GenericType<>(){};
    }
}
