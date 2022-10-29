package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.claimcommunitypoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.ClaimCommunityPointsPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ClaimCommunityPointsData{
	@JsonProperty("claimCommunityPoints")
	@NotNull
	private ClaimCommunityPointsPayload claimCommunityPoints;
}
