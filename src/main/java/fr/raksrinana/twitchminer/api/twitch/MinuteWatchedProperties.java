package fr.raksrinana.twitchminer.api.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MinuteWatchedProperties{
	@JsonProperty("channel_id")
	private String channelId;
	@JsonProperty("broadcast_id")
	private String broadcastId;
	@JsonProperty("player")
	private String player;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("game")
	private String game;
}
