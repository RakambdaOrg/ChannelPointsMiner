package fr.raksrinana.channelpointsminer.api.ws.data.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.channelpointsminer.api.ws.data.message.Message;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.util.json.JacksonUtils;
import lombok.*;
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
	private Message message;
	
	@JsonProperty("message")
	public void setMessage(String value) throws IOException{
		message = JacksonUtils.read(value, new TypeReference<>(){});
	}
}
