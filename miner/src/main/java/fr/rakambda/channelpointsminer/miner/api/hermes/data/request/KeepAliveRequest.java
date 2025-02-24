package fr.rakambda.channelpointsminer.miner.api.hermes.data.request;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class KeepAliveRequest extends ITwitchHermesWebSocketRequest{
	public KeepAliveRequest(){
		super("keepalive");
	}
}
