package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.claimclaimed.ClaimClaimedData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("claim-claimed")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class ClaimClaimed extends IMessage{
	@JsonProperty("data")
	@NotNull
	private ClaimClaimedData data;
}
