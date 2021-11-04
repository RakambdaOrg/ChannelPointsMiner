package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.util.json.TwitchTimestampDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.time.Instant;

@JsonTypeName("stream-up")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamUp extends Message{
	@JsonProperty("server_time")
	@JsonDeserialize(using = TwitchTimestampDeserializer.class)
	private Instant serverTime;
	@JsonProperty("play_delay")
	private int playDelay;
}
