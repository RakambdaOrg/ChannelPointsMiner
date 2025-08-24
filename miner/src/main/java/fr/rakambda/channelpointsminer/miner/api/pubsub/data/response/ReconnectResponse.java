package fr.rakambda.channelpointsminer.miner.api.pubsub.data.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("RECONNECT")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ReconnectResponse extends ITwitchWebSocketResponse{
}
