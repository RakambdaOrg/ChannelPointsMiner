package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__typename")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = Channel.class, name = "Channel"),
		@JsonSubTypes.Type(value = ChannelSelfEdge.class, name = "ChannelSelfEdge"),
		@JsonSubTypes.Type(value = CommunityPointsAutomaticReward.class, name = "CommunityPointsAutomaticReward"),
		@JsonSubTypes.Type(value = CommunityPointsChannelEarningSettings.class, name = "CommunityPointsChannelEarningSettings"),
		@JsonSubTypes.Type(value = CommunityPointsChannelSettings.class, name = "CommunityPointsChannelSettings"),
		@JsonSubTypes.Type(value = CommunityPointsCommunityGoal.class, name = "CommunityPointsCommunityGoal"),
		@JsonSubTypes.Type(value = CommunityPointsCustomReward.class, name = "CommunityPointsCustomReward"),
		@JsonSubTypes.Type(value = CommunityPointsCustomRewardGlobalCooldownSetting.class, name = "CommunityPointsCustomRewardGlobalCooldownSetting"),
		@JsonSubTypes.Type(value = CommunityPointsCustomRewardMaxPerStreamSetting.class, name = "CommunityPointsCustomRewardMaxPerStreamSetting"),
		@JsonSubTypes.Type(value = CommunityPointsCustomRewardMaxPerUserPerStreamSetting.class, name = "CommunityPointsCustomRewardMaxPerUserPerStreamSetting"),
		@JsonSubTypes.Type(value = CommunityPointsCustomRewardUserRedemption.class, name = "CommunityPointsCustomRewardUserRedemption"),
		@JsonSubTypes.Type(value = CommunityPointsEmote.class, name = "CommunityPointsEmote"),
		@JsonSubTypes.Type(value = CommunityPointsEmoteModification.class, name = "CommunityPointsEmoteModification"),
		@JsonSubTypes.Type(value = CommunityPointsEmoteVariant.class, name = "CommunityPointsEmoteVariant"),
		@JsonSubTypes.Type(value = CommunityPointsImage.class, name = "CommunityPointsImage"),
		@JsonSubTypes.Type(value = CommunityPointsLastViewedContentByType.class, name = "CommunityPointsLastViewedContentByType"),
		@JsonSubTypes.Type(value = CommunityPointsLastViewedContentByTypeAndID.class, name = "CommunityPointsLastViewedContentByTypeAndID"),
		@JsonSubTypes.Type(value = CommunityPointsMultiplier.class, name = "CommunityPointsMultiplier"),
		@JsonSubTypes.Type(value = CommunityPointsProperties.class, name = "CommunityPointsProperties"),
		@JsonSubTypes.Type(value = CommunityPointsUserProperties.class, name = "CommunityPointsUserProperties"),
		@JsonSubTypes.Type(value = CommunityPointsWatchStreakEarningSettings.class, name = "CommunityPointsWatchStreakEarningSettings"),
		@JsonSubTypes.Type(value = RequestInfo.class, name = "RequestInfo"),
		@JsonSubTypes.Type(value = User.class, name = "User"),
		@JsonSubTypes.Type(value = UserSelfConnection.class, name = "UserSelfConnection"),
})
public abstract class GQLType{
	@JsonProperty("__typename")
	@NotNull
	private String typename;
}
