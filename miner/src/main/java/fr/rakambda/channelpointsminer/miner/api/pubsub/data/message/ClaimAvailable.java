package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.claimavailable.ClaimAvailableData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("claim-available")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class ClaimAvailable extends IPubSubMessage{
	@JsonProperty("data")
	@NotNull
	private ClaimAvailableData data;
}
