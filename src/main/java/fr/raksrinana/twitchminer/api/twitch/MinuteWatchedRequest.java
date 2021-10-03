package fr.raksrinana.twitchminer.api.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MinuteWatchedRequest{
	@JsonProperty("event")
	private final String event = "minute-watched";
	@JsonProperty("properties")
	private MinuteWatchedProperties properties;
}
