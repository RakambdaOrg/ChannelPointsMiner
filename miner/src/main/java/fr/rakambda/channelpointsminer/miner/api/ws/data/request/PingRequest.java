package fr.rakambda.channelpointsminer.miner.api.ws.data.request;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class PingRequest extends ITwitchWebSocketRequest{
	public PingRequest(){
		super("PING");
	}
}
