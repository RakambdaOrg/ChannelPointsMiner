package fr.rakambda.channelpointsminer.miner.api.hermes.data.message;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.TwitchTimestampDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("stream-down")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class StreamDown extends IHermesMessage {
	@JsonProperty("server_time")
	@JsonDeserialize(using = TwitchTimestampDeserializer.class)
	private Instant serverTime;
}
