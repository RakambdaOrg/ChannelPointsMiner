package fr.rakambda.channelpointsminer.miner.api.hermes.data.request.subscribe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@JsonTypeName("pubsub")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PubSubSubscribeType extends SubscribeData{
	@JsonProperty("type")
	private String type;
	@JsonProperty("pubsub")
	private PubSub pubsub;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	public class PubSub{
		@JsonProperty("topic")
		private String topic;
	}
	
	public PubSubSubscribeType(@NonNull String topic){
		super();
		type = "pubsub";
		pubsub = new PubSub(topic);
	}
}
