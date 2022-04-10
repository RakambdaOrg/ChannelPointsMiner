package fr.raksrinana.channelpointsminer.miner.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
