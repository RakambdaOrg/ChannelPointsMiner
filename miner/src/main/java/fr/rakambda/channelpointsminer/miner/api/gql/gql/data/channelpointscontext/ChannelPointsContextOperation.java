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
        addPersistedQueryExtension(new PersistedQueryExtension(1, "33884dcfeab64eb44c2d61255145643cf69ea993e0c6c2ecb0fa46238586a111"));
        addVariable("channelLogin", username);
        addVariable("includeGoalTypes", List.of("CREATOR", "BOOST"));
    }
    
    @Override
    @NonNull
    public GenericType<GQLResponse<ChannelPointsContextData>> getResponseType(){
        return new GenericType<>(){};
    }
}
