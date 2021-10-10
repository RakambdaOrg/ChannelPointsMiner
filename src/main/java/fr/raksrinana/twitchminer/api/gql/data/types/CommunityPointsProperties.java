package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.UnknownDeserializer;
import lombok.*;
import java.util.List;

@JsonTypeName("CommunityPointsProperties")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsProperties extends GQLType{
	@JsonProperty("availableClaim")
	@JsonDeserialize(using = UnknownDeserializer.class)
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
}
