package fr.raksrinana.channelpointsminer.api.ws.data.request;

import lombok.Getter;

@Getter
public class PingRequest extends TwitchWebSocketRequest{
	public PingRequest(){
		super("PING");
	}
}
