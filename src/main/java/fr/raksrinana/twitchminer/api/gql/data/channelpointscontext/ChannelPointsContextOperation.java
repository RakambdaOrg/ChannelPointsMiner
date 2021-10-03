package fr.raksrinana.twitchminer.api.gql.data.channelpointscontext;

import fr.raksrinana.twitchminer.api.gql.data.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.PersistedQueryExtension;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import kong.unirest.GenericType;
import org.jetbrains.annotations.NotNull;

public class ChannelPointsContextOperation extends GQLOperation<ChannelPointsContextData>{
	public ChannelPointsContextOperation(String username){
		super("ChannelPointsContext");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "9988086babc615a918a1e9a722ff41d98847acac822645209ac7379eecb27152"));
		addVariable("channelLogin", username);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<ChannelPointsContextData>> getResponseType(){
		return new GenericType<>(){};
	}
}
