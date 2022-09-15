package fr.raksrinana.channelpointsminer.miner.api.ws.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

@Getter
public class UnlistenTopicRequest extends ITwitchWebSocketRequest{
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
