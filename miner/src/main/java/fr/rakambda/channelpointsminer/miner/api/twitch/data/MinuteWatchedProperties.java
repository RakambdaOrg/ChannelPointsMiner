package fr.rakambda.channelpointsminer.miner.api.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MinuteWatchedProperties{
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
	@JsonProperty("channel")
	@NotNull
	private String channel;
	@JsonProperty("broadcast_id")
	@NotNull
	private String broadcastId;
	@JsonProperty("player")
	@NotNull
	private String player;
	@JsonProperty("user_id")
	private int userId;
	@JsonProperty("game")
	@Nullable
	private String game;
	@JsonProperty("game_id")
	@Nullable
	private String gameId;
	@JsonProperty("live")
	private boolean live;
}
