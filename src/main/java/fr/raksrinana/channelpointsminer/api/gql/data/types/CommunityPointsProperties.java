package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
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
	@Nullable
	private CommunityPointsClaim availableClaim;
	@JsonProperty("balance")
	private int balance;
	@JsonProperty("activeMultipliers")
	@NotNull
	@Builder.Default
	private List<CommunityPointsMultiplier> activeMultipliers = new ArrayList<>();
	@JsonProperty("canRedeemRewardsForFree")
	private boolean canRedeemRewardsForFree;
	@JsonProperty("lastViewedContent")
	@NotNull
	@Builder.Default
	private List<CommunityPointsLastViewedContentByType> lastViewedContent = new ArrayList<>();
	@JsonProperty("userRedemptions")
	@NotNull
	@Builder.Default
	private List<CommunityPointsCustomRewardUserRedemption> userRedemptions = new ArrayList<>();
}
