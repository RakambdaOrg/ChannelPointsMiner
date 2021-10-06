package fr.raksrinana.twitchminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.twitchminer.api.ws.data.message.Message;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.IOException;

@Getter
@NoArgsConstructor
@ToString
public class MessageData{
	@JsonProperty("topic")
	private Topic topic;
	
	@JsonIgnore
	private Message message;
	
	@JsonProperty("message")
	public void setMessage(String value) throws IOException{
		message = JacksonUtils.read(value, new TypeReference<>(){});
	}
}
