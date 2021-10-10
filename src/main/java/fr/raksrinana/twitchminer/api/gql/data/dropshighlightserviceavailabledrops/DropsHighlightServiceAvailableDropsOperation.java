package fr.raksrinana.twitchminer.api.gql.data.dropshighlightserviceavailabledrops;

import fr.raksrinana.twitchminer.api.gql.data.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropsHighlightServiceAvailableDropsOperation extends GQLOperation<DropsHighlightServiceAvailableDropsData>{
	public DropsHighlightServiceAvailableDropsOperation(String username){
		super("DropsHighlightService_AvailableDrops");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "b19ee96a0e79e3f8281c4108bc4c7b3f232266db6f96fd04a339ab393673a075"));
		addVariable("channelID", username);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<DropsHighlightServiceAvailableDropsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
