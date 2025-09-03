package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.communitymomentcalloutclaim;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ClaimCommunityMomentPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class CommunityMomentCalloutClaimData{
	@JsonProperty("claimCommunityMoment")
	@NonNull
	private ClaimCommunityMomentPayload moment;
}
