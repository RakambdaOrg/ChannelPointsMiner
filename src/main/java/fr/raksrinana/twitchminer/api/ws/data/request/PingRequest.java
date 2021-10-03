package fr.raksrinana.twitchminer.api.ws.data.request;

import lombok.Getter;

@Getter
public class PingRequest extends TwitchWebSocketRequest{
	public PingRequest(){
		super("PING");
	}
}
