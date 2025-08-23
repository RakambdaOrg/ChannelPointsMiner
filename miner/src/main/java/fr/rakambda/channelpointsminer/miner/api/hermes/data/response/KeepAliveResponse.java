package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("keepalive")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@ToString
public class KeepAliveResponse extends ITwitchHermesWebSocketResponse{
}
