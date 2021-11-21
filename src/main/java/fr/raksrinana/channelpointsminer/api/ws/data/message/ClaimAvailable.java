package fr.raksrinana.channelpointsminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.claimavailable.ClaimAvailableData;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("claim-available")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class ClaimAvailable extends IMessage{
	@JsonProperty("data")
	@NotNull
	private ClaimAvailableData data;
}
