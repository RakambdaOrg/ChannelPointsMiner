package fr.raksrinana.channelpointsminer.api.gql.data.channelpointscontext;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ChannelPointsContextOperation extends IGQLOperation<ChannelPointsContextData>{
	public ChannelPointsContextOperation(@NotNull String username){
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
