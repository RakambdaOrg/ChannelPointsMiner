package fr.raksrinana.twitchminer.api.ws.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import org.jetbrains.annotations.NotNull;

@Getter
public class ListenTopicRequest extends TwitchWebSocketRequest{
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
