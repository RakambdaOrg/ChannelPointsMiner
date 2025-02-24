package fr.rakambda.channelpointsminer.miner.api.hermes.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.time.Instant;

@ToString
@Getter
public abstract class ITwitchHermesWebSocketRequest{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("timestamp")
	@NotNull
	private Instant timestamp;
	@JsonProperty("type")
	@NotNull
	private String type;
	
	public ITwitchHermesWebSocketRequest(@NotNull String type){
		id = CommonUtils.randomAlphanumeric(21);
		timestamp = TimeFactory.now();
		this.type = type;
	}
}
