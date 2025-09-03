package fr.rakambda.channelpointsminer.miner.api.hermes.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.util.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@ToString(callSuper = true)
@Getter
public class UnsubscribeRequest extends ITwitchHermesWebSocketRequest{
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	public class Data{
		@JsonProperty("id")
		private String id;
	}
	
	@JsonProperty("unsubscribe")
	private Data unsubscribe;
	
	public UnsubscribeRequest(@NonNull String id){
		super("unsubscribe");
		unsubscribe = new Data(id);
	}
}
