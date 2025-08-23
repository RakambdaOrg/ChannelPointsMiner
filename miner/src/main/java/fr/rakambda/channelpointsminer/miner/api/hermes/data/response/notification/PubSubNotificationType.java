package fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.io.IOException;

@JsonTypeName("pubsub")
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PubSubNotificationType extends NotificationData{
	@JsonIgnore
	private IPubSubMessage pubsub;
	
	@JsonProperty("pubsub")
	public void setPubsub(String value) throws IOException{
		pubsub = JacksonUtils.read(value, new TypeReference<>(){});
	}
}
