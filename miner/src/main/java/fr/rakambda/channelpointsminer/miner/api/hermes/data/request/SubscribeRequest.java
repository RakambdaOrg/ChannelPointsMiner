package fr.rakambda.channelpointsminer.miner.api.hermes.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.subscribe.PubSubSubscribeType;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.subscribe.SubscribeData;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
@Getter
public class SubscribeRequest extends ITwitchHermesWebSocketRequest{
	@JsonProperty("subscribe")
	private SubscribeData subscribe;
	
	public SubscribeRequest(@NotNull SubscribeData data){
		super("subscribe");
		subscribe = data;
	}
	
	public static SubscribeRequest pubsub(@NotNull String topic){
		return new SubscribeRequest(new PubSubSubscribeType(topic));
	}
}
