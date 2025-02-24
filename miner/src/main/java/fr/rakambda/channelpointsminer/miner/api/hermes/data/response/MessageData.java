package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.message.IHermesMessage;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
	private IHermesMessage message;
	
	@JsonProperty("message")
	public void setMessage(String value) throws IOException{
		message = JacksonUtils.read(value, new TypeReference<>(){});
	}
}
