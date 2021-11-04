package fr.raksrinana.channelpointsminer.api.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("minute-watched")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class MinuteWatchedEvent extends PlayerEvent{
	@JsonProperty("properties")
	private MinuteWatchedProperties properties;
}
