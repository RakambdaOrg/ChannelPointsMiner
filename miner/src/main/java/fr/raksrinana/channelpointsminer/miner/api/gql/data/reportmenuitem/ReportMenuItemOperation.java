package fr.raksrinana.channelpointsminer.miner.api.gql.data.reportmenuitem;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ReportMenuItemOperation extends IGQLOperation<ReportMenuItemData>{
	public ReportMenuItemOperation(@NotNull String username){
		super("ReportMenuItem");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "8f3628981255345ca5e5453dfd844efffb01d6413a9931498836e6268692a30c"));
		addVariable("channelLogin", username);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<ReportMenuItemData>> getResponseType(){
		return new GenericType<>(){};
	}
}
