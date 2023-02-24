package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.channelfollows;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ChannelFollowsOperation extends IGQLOperation<ChannelFollowsData>{
	public ChannelFollowsOperation(int limit, @NotNull String order, @Nullable String cursor){
		super("ChannelFollows");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "eecf815273d3d949e5cf0085cc5084cd8a1b5b7b6f7990cf43cb0beadf546907"));
		addVariable("limit", limit);
		addVariable("order", order);
		if(Objects.nonNull(cursor)){
			addVariable("cursor", cursor);
		}
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<ChannelFollowsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
