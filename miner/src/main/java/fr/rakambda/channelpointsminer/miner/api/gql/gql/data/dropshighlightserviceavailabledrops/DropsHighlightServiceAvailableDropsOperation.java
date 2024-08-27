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
	    addPersistedQueryExtension(new PersistedQueryExtension(1, "eff13f4a43157238e40b4cd74b0dac3a41b5f8fb31de1a3b19347fae84e60b92"));
        addVariable("channelID", channelId);
    }
    
    @Override
    @NotNull
    public GenericType<GQLResponse<DropsHighlightServiceAvailableDropsData>> getResponseType(){
        return new GenericType<>(){};
    }
}
