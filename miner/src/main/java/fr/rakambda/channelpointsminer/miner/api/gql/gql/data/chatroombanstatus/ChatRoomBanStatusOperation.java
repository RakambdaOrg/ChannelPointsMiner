package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ChatRoomBanStatusOperation extends IGQLOperation<ChatRoomBanStatusData>{
	public ChatRoomBanStatusOperation(@NonNull String channelId, @NonNull String targetUserId){
		super("ChatRoomBanStatus");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "319f2a9a3ac7ddecd7925944416c14b818b65676ab69da604460b68938d22bea"));
		addVariable("targetUserID", targetUserId);
		addVariable("channelID", channelId);
	}
	
	@Override
	@NonNull
	public GenericType<GQLResponse<ChatRoomBanStatusData>> getResponseType(){
		return new GenericType<>(){};
	}
}
