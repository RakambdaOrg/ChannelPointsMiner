package fr.raksrinana.channelpointsminer.api.gql.data.dropspageclaimdroprewards;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.api.gql.data.types.ClaimDropRewardsPayload;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class DropsPageClaimDropRewardsData{
	@JsonProperty("claimDropRewards")
	@NotNull
	private ClaimDropRewardsPayload claimDropRewards;
}
