package fr.raksrinana.twitchminer.api.gql.data.claimcommunitypoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.ClaimCommunityPointsPayload;
import lombok.*;
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
