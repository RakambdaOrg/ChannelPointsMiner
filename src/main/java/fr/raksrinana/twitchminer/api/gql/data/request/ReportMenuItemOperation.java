package fr.raksrinana.twitchminer.api.gql.data.request;

import fr.raksrinana.twitchminer.api.gql.data.response.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.response.reportmenuitem.ReportMenuItemData;
import kong.unirest.GenericType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ReportMenuItemOperation extends GQLOperation<ReportMenuItemData>{
	public ReportMenuItemOperation(String username){
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
