package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ChatRoomBanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ChatRoomBanStatusData{
	@JsonProperty("chatRoomBanStatus")
	@Nullable
	private ChatRoomBanStatus chatRoomBanStatus;
}
