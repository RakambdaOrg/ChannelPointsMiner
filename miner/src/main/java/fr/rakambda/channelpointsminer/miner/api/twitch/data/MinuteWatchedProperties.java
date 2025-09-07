package fr.rakambda.channelpointsminer.miner.api.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MinuteWatchedProperties{
	@JsonProperty("broadcast_id")
	@NonNull
	private String broadcastId;
	@JsonProperty("channel")
	@NonNull
	private String channel;
	@JsonProperty("channel_id")
	@NonNull
	private String channelId;
	@JsonProperty("game")
	@Nullable
	private String game;
	@JsonProperty("game_id")
	@Nullable
	private String gameId;
	@JsonProperty("live")
	private boolean live;
	@JsonProperty("hidden")
	private boolean hidden;
	@JsonProperty("player")
	@NonNull
	private String player;
	@JsonProperty("user_id")
	private int userId;
	@JsonProperty("location")
	private String location;
	@JsonProperty("logged_in")
	private boolean loggedIn;
	@JsonProperty("muted")
	private boolean muted;
}
