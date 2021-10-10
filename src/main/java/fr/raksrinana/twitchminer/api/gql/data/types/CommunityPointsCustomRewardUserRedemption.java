package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("CommunityPointsCustomRewardUserRedemption")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsCustomRewardUserRedemption extends GQLType{
	@JsonProperty("reward")
	private CommunityPointsCustomReward reward;
	@JsonProperty("userRedemptionsCurrentStream")
	private Integer userRedemptionsCurrentStream;
}
