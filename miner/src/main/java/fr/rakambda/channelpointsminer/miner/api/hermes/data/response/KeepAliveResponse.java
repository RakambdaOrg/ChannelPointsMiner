package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@JsonTypeName("keepalive")
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@ToString(callSuper = true)
public class KeepAliveResponse extends ITwitchHermesWebSocketResponse{
}
