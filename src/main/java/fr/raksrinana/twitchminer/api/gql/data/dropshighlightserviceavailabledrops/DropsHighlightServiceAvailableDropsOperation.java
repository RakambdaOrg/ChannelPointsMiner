package fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops;

import fr.raksrinana.twitchminer.api.gql.data.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.GenericType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class DropsHighlightServiceAvailableDropsOperation extends GQLOperation<DropsHighlightServiceAvailableDropsData>{
	public DropsHighlightServiceAvailableDropsOperation(String username){
		super("DropsHighlightService_AvailableDrops");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "b19ee96a0e79e3f8281c4108bc4c7b3f232266db6f96fd04a339ab393673a075"));
		addVariable("channel", username);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<DropsHighlightServiceAvailableDropsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
