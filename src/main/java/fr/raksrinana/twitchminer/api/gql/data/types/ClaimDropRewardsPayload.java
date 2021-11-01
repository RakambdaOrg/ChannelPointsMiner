package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("ClaimDropRewardsPayload")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimDropRewardsPayload extends GQLType{
	@JsonProperty("isUserAccountConnected")
	private boolean isUserAccountConnected;
	@JsonProperty("status")
	private ClaimDropRewardsStatus status;
}
