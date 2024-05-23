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
	    addPersistedQueryExtension(new PersistedQueryExtension(1, "962510a535f25f33bbf85d7767982e3bb6d1b00f84dd3c7a06d8572323dfd010"));
        addVariable("channelID", channelId);
    }
    
    @Override
    @NotNull
    public GenericType<GQLResponse<DropsHighlightServiceAvailableDropsData>> getResponseType(){
        return new GenericType<>(){};
    }
}
