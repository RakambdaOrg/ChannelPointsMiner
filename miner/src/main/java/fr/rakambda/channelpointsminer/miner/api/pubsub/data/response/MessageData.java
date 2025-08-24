package fr.rakambda.channelpointsminer.miner.api.pubsub.data.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.IOException;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class MessageData{
	@JsonProperty("topic")
	private Topic topic;
	
	@JsonIgnore
	private IPubSubMessage message;
	
	@JsonProperty("message")
	public void setMessage(String value) throws IOException{
		message = JacksonUtils.read(value, new TypeReference<>(){});
	}
}
