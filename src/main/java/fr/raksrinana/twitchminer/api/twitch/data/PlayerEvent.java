package fr.raksrinana.twitchminer.api.twitch.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "event")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = MinuteWatchedEvent.class, name = "minute-watched"),
})
@EqualsAndHashCode
@ToString
public abstract class PlayerEvent{
}
