package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("MESSAGE")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageResponseHermes extends ITwitchHermesWebSocketResponse {
	@JsonProperty("data")
	private MessageData data;
}
