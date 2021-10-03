package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("CommunityPointsCustomRewardUserRedemption")
@Getter
@AllArgsConstructor
public class CommunityPointsCustomRewardUserRedemption extends GQLType{
	@JsonProperty("reward")
	private CommunityPointsCustomReward reward;
	@JsonProperty("userRedemptionsCurrentStream")
	private Integer userRedemptionsCurrentStream;
	
	public CommunityPointsCustomRewardUserRedemption(){
		super("CommunityPointsCustomRewardUserRedemption");
	}
}
