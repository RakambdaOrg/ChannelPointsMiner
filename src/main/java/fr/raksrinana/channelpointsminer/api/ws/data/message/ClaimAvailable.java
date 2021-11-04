package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.claimavailable.ClaimAvailableData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("claim-available")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimAvailable extends Message{
	@JsonProperty("data")
	private ClaimAvailableData data;
}
