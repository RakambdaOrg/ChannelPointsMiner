package fr.rakambda.channelpointsminer.miner.api.hermes.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.topic.Topics;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
@Getter
public class UnlistenTopicRequest extends ITwitchHermesWebSocketRequest{
	private static final int NONCE_LENGTH = 30;
	
	@JsonProperty("nonce")
	private String nonce;
	@JsonProperty("data")
	private Topics data;
	
	public UnlistenTopicRequest(@NotNull Topics topics){
		super("UNLISTEN");
		data = topics;
		nonce = CommonUtils.randomAlphanumeric(NONCE_LENGTH);
	}
}
