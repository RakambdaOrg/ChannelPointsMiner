package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = PongResponseHermes.class, name = "PONG"),
		@JsonSubTypes.Type(value = AuthenticateResponse.class, name = "authenticateResponse"),
})
@ToString
@EqualsAndHashCode
public abstract class ITwitchHermesWebSocketResponse {
	@JsonProperty("id")
	private String id;
	@JsonProperty("parentId")
	private String parentId;
	@JsonProperty("timestamp")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime timestamp;
}
