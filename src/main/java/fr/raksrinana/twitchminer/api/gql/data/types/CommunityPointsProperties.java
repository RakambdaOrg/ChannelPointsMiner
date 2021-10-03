package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@JsonTypeName("CommunityPointsProperties")
@Getter
@AllArgsConstructor
public class CommunityPointsProperties extends GQLType{
	@JsonProperty("availableClaim")
	private Object availableClaim;
	@JsonProperty("balance")
	private int balance;
	@JsonProperty("activeMultipliers")
	private List<CommunityPointsMultiplier> activeMultipliers;
	@JsonProperty("canRedeemRewardsForFree")
	private boolean canRedeemRewardsForFree;
	@JsonProperty("lastViewedContent")
	private List<CommunityPointsLastViewedContentByType> lastViewedContent;
	@JsonProperty("userRedemptions")
	private List<CommunityPointsCustomRewardUserRedemption> userRedemptions;
	
	public CommunityPointsProperties(){
		super("CommunityPointsProperties");
	}
}
