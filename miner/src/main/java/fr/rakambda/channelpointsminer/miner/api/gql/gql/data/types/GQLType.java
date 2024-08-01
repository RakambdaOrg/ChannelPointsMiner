package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__typename")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = BroadcastSettings.class, name = "BroadcastSettings"),
        @JsonSubTypes.Type(value = Channel.class, name = "Channel"),
        @JsonSubTypes.Type(value = ChannelSelfEdge.class, name = "ChannelSelfEdge"),
        @JsonSubTypes.Type(value = CommunityPointsMultiplier.class, name = "CommunityPointsMultiplier"),
        @JsonSubTypes.Type(value = CommunityPointsProperties.class, name = "CommunityPointsProperties"),
        @JsonSubTypes.Type(value = Game.class, name = "Game"),
        @JsonSubTypes.Type(value = Stream.class, name = "Stream"),
        @JsonSubTypes.Type(value = User.class, name = "User"),
        @JsonSubTypes.Type(value = Inventory.class, name = "Inventory"),
        @JsonSubTypes.Type(value = DropCampaign.class, name = "DropCampaign"),
        @JsonSubTypes.Type(value = TimeBasedDrop.class, name = "TimeBasedDrop"),
        @JsonSubTypes.Type(value = DropBenefitEdge.class, name = "DropBenefitEdge"),
        @JsonSubTypes.Type(value = DropBenefit.class, name = "DropBenefit"),
        @JsonSubTypes.Type(value = CommunityPointsClaim.class, name = "CommunityPointsClaim"),
        @JsonSubTypes.Type(value = TimeBasedDropSelfEdge.class, name = "TimeBasedDropSelfEdge"),
        @JsonSubTypes.Type(value = UserDropReward.class, name = "UserDropReward"),
        @JsonSubTypes.Type(value = MakePredictionPayload.class, name = "MakePredictionPayload"),
        @JsonSubTypes.Type(value = MakePredictionError.class, name = "MakePredictionError"),
        @JsonSubTypes.Type(value = FollowConnection.class, name = "FollowConnection"),
        @JsonSubTypes.Type(value = PageInfo.class, name = "PageInfo"),
        @JsonSubTypes.Type(value = FollowEdge.class, name = "FollowEdge"),
        @JsonSubTypes.Type(value = ChatRoomBanStatus.class, name = "ChatRoomBanStatus"),
        @JsonSubTypes.Type(value = ClaimCommunityMomentPayload.class, name = "ClaimCommunityMomentPayload"),
        @JsonSubTypes.Type(value = StreamPlaybackAccessToken.class, name = "PlaybackAccessToken"),
        @JsonSubTypes.Type(value = DropCampaignSummary.class, name = "DropCampaignSummary"),
        @JsonSubTypes.Type(value = SetDropsCommunityHighlightToHiddenPayload.class, name = "SetDropsCommunityHighlightToHiddenPayload"),
})
@EqualsAndHashCode
public abstract class GQLType{
}
