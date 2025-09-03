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
public class ReportMenuItemOperation extends IGQLOperation<ReportMenuItemData>{
	public ReportMenuItemOperation(@NonNull String username){
		super("ReportMenuItem");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "8f3628981255345ca5e5453dfd844efffb01d6413a9931498836e6268692a30c"));
		addVariable("channelLogin", username);
	}
	
	@Override
	@NonNull
	public GenericType<GQLResponse<ReportMenuItemData>> getResponseType(){
		return new GenericType<>(){};
	}
}
