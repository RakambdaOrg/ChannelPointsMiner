package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("PONG")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class PongResponseHermes extends ITwitchHermesWebSocketResponse {
}
