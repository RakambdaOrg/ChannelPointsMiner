package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.withislive;

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
public class WithIsStreamLiveOperation extends IGQLOperation<WithIsStreamLiveData>{
	public WithIsStreamLiveOperation(@NotNull String id){
		super("WithIsStreamLiveQuery");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "04e46329a6786ff3a81c01c50bfa5d725902507a0deb83b0edbf7abe7a3716ea"));
		addVariable("id", id);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<WithIsStreamLiveData>> getResponseType(){
		return new GenericType<>(){};
	}
}
