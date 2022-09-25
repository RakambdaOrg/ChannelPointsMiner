package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.chatroombanstatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.ChatRoomBanStatus;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	@JsonProperty("targetUser")
	@NotNull
	private User targetUser;
}
