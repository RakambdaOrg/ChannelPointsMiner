package fr.raksrinana.channelpointsminer.miner.api.ws.data.request;

import lombok.Getter;

@Getter
public class PingRequest extends ITwitchWebSocketRequest{
	public PingRequest(){
		super("PING");
	}
}
