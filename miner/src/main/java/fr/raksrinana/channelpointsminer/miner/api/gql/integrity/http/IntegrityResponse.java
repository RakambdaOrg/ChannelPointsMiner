package fr.raksrinana.channelpointsminer.miner.api.gql.integrity.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.MillisecondsTimestampDeserializer;
import fr.raksrinana.channelpointsminer.miner.util.json.UnknownDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class IntegrityResponse{
	@JsonProperty("expiration")
	@JsonDeserialize(using = MillisecondsTimestampDeserializer.class)
	private Instant expiration;
	@JsonProperty("request_id")
	private String requestId;
	@JsonProperty("token")
	private String token;
	@JsonProperty("error")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object error;
	@JsonProperty("message")
	private String message;
}
