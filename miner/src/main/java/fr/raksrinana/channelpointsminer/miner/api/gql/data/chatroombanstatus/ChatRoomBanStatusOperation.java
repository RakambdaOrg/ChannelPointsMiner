package fr.raksrinana.channelpointsminer.miner.api.gql.data.chatroombanstatus;

import fr.raksrinana.channelpointsminer.miner.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ChatRoomBanStatusOperation extends IGQLOperation<ChatRoomBanStatusData>{
	public ChatRoomBanStatusOperation(@NotNull String channelId, @NotNull String targetUserId){
		super("ChatRoomBanStatus");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "319f2a9a3ac7ddecd7925944416c14b818b65676ab69da604460b68938d22bea"));
		addVariable("targetUserID", targetUserId);
		addVariable("channelID", channelId);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<ChatRoomBanStatusData>> getResponseType(){
		return new GenericType<>(){};
	}
}
