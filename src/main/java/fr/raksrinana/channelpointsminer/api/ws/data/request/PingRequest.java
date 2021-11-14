package fr.raksrinana.channelpointsminer.api.ws.data.request;

import lombok.Getter;

@Getter
public class PingRequest extends ITwitchWebSocketRequest{
	public PingRequest(){
		super("PING");
	}
}
