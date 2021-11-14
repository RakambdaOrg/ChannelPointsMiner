package fr.raksrinana.channelpointsminer.api.ws.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topics;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

@Getter
public class ListenTopicRequest extends ITwitchWebSocketRequest{
	private static final int NONCE_LENGTH = 30;
	
	@JsonProperty("nonce")
	private String nonce;
	@JsonProperty("data")
	private Topics data;
	
	public ListenTopicRequest(@NotNull Topics topics){
		super("LISTEN");
		data = topics;
		nonce = RandomStringUtils.randomAlphanumeric(NONCE_LENGTH);
	}
}
