package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropsHighlightServiceAvailableDropsOperation extends IGQLOperation<DropsHighlightServiceAvailableDropsData>{
    public DropsHighlightServiceAvailableDropsOperation(@NotNull String channelId){
        super("DropsHighlightService_AvailableDrops");
        addPersistedQueryExtension(new PersistedQueryExtension(1, "e589e213f16d9b17c6f0a8ccd18bdd6a8a6b78bc9db67a75efd43793884ff4e5"));
        addVariable("channelID", channelId);
    }
    
    @Override
    @NotNull
    public GenericType<GQLResponse<DropsHighlightServiceAvailableDropsData>> getResponseType(){
        return new GenericType<>(){};
    }
}
