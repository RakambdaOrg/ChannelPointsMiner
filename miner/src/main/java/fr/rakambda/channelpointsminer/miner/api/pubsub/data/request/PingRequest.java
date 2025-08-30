package fr.rakambda.channelpointsminer.miner.api.pubsub.data.request;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class PingRequest extends ITwitchWebSocketRequest{
	public PingRequest(){
		super("PING");
	}
}
