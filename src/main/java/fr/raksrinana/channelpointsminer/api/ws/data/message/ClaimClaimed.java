package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.claimclaimed.ClaimClaimedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("claim-claimed")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimClaimed extends Message{
	@JsonProperty("data")
	private ClaimClaimedData data;
}
