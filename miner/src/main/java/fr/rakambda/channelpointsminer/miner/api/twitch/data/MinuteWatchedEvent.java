package fr.rakambda.channelpointsminer.miner.api.twitch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
