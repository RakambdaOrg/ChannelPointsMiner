package fr.raksrinana.channelpointsminer.api.gql.data.dropshighlightserviceavailabledrops;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropsHighlightServiceAvailableDropsOperation extends IGQLOperation<DropsHighlightServiceAvailableDropsData>{
	public DropsHighlightServiceAvailableDropsOperation(@NotNull String channelId){
		super("DropsHighlightService_AvailableDrops");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "b19ee96a0e79e3f8281c4108bc4c7b3f232266db6f96fd04a339ab393673a075"));
		addVariable("channelID", channelId);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<DropsHighlightServiceAvailableDropsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
