package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@EqualsAndHashCode(callSuper = true)
@ToString
public class GetUserIdFromLoginOperation extends IGQLOperation<GetUserIdFromLoginData>{
	public GetUserIdFromLoginOperation(@NonNull String username){
		super("GetUserIDFromLogin");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "c8502d09d4f290bb5155e6953a2c3119d4296d7ce647a2e21d1cf4c805583e43"));
		addVariable("login", username);
		addVariable("lookupType", "ACTIVE");
	}
	
	@Override
	@NonNull
	public GenericType<GQLResponse<GetUserIdFromLoginData>> getResponseType(){
		return new GenericType<>(){};
	}
}
