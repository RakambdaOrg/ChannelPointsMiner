package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("subscribeResponse")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SubscribeResponse extends ITwitchHermesWebSocketResponse{
	@JsonProperty("subscribeResponse")
	private SubscribeResponseData subscribeResponse;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	@Builder
	public static class SubscribeResponseData{
		@JsonProperty("result")
		private String result;
	}
}
