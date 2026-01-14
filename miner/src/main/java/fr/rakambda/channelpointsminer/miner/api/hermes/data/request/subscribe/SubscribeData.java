package fr.rakambda.channelpointsminer.miner.api.hermes.data.request.subscribe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.*;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

import java.time.ZonedDateTime;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = PubSubSubscribeType.class, name = "pubsub"),
})
@ToString
@EqualsAndHashCode
public abstract class SubscribeData {
	@JsonProperty("id")
	private String id;
	
	protected SubscribeData(){
		id = CommonUtils.randomAlphanumeric(21);
	}
}
