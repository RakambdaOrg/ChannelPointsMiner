package fr.rakambda.channelpointsminer.miner.api.hermes.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.time.Instant;

@ToString
@Getter
public abstract class ITwitchHermesWebSocketRequest{
	@JsonProperty("id")
	@NonNull
	private String id;
	@JsonProperty("timestamp")
	@NonNull
	private Instant timestamp;
	@JsonProperty("type")
	@NonNull
	private String type;
	
	public ITwitchHermesWebSocketRequest(@NonNull String type){
		id = CommonUtils.randomAlphanumeric(21);
		timestamp = TimeFactory.now();
		this.type = type;
	}
}
