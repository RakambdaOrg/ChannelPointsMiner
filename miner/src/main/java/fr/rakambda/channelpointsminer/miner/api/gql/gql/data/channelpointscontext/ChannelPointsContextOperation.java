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
        addPersistedQueryExtension(new PersistedQueryExtension(1, "1530a003a7d374b0380b79db0be0534f30ff46e61cffa2bc0e2468a909fbc024"));
        addVariable("channelLogin", username);
        addVariable("includeGoalTypes", List.of("CREATOR", "BOOST"));
    }
    
    @Override
    @NonNull
    public GenericType<GQLResponse<ChannelPointsContextData>> getResponseType(){
        return new GenericType<>(){};
    }
}
