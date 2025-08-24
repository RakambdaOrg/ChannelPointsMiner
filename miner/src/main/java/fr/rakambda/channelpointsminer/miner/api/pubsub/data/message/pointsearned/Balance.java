package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.pointsearned;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Balance{
	@JsonProperty("channel_id")
	private String channelId;
	@JsonProperty("balance")
	private int balance;
}
