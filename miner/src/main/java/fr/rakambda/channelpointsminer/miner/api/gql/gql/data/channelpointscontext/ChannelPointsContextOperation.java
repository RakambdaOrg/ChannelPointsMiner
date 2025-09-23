package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelpointscontext;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ChannelPointsContextOperation extends IGQLOperation<ChannelPointsContextData>{
    public ChannelPointsContextOperation(@NonNull String username){
        super("ChannelPointsContext");
        addPersistedQueryExtension(new PersistedQueryExtension(1, "374314de591e69925fce3ddc2bcf085796f56ebb8cad67a0daa3165c03adc345"));
        addVariable("channelLogin", username);
        addVariable("includeGoalTypes", List.of("CREATOR", "BOOST"));
    }
    
    @Override
    @NonNull
    public GenericType<GQLResponse<ChannelPointsContextData>> getResponseType(){
        return new GenericType<>(){};
    }
}
