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
	    addPersistedQueryExtension(new PersistedQueryExtension(1, "9a62a09bce5b53e26e64a671e530bc599cb6aab1e5ba3cbd5d85966d3940716f"));
        addVariable("channelID", channelId);
    }
    
    @Override
    @NotNull
    public GenericType<GQLResponse<DropsHighlightServiceAvailableDropsData>> getResponseType(){
        return new GenericType<>(){};
    }
}
