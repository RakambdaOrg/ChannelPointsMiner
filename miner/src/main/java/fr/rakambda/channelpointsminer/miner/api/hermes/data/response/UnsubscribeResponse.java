package fr.rakambda.channelpointsminer.miner.api.hermes.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("unsubscribeResponse")
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UnsubscribeResponse extends ITwitchHermesWebSocketResponse{
	@JsonProperty("unsubscribeResponse")
	private UnsubscribeResponseData unsubscribeResponse;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	@Builder
	public static class UnsubscribeResponseData{
		@JsonProperty("result")
		private String result;
		@JsonProperty("subscription")
		private SubscriptionData subscription;
	}
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	@Builder
	public static class SubscriptionData{
		@JsonProperty("id")
		private String id;
	}
}
