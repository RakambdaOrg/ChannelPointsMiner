package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = PointsEarned.class, name = "points-earned"),
		@JsonSubTypes.Type(value = ClaimAvailable.class, name = "claim-available"),
		@JsonSubTypes.Type(value = StreamDown.class, name = "stream-down"),
		@JsonSubTypes.Type(value = StreamUp.class, name = "stream-up"),
		@JsonSubTypes.Type(value = EventUpdated.class, name = "event-updated"),
		@JsonSubTypes.Type(value = EventCreated.class, name = "event-created"),
		@JsonSubTypes.Type(value = RaidUpdateV2.class, name = "raid_update_v2"),
		@JsonSubTypes.Type(value = PointsSpent.class, name = "points-spent"),
		@JsonSubTypes.Type(value = PredictionMade.class, name = "prediction-made"),
		@JsonSubTypes.Type(value = PredictionResult.class, name = "prediction-result"),
		@JsonSubTypes.Type(value = PredictionUpdated.class, name = "prediction-updated"),
		@JsonSubTypes.Type(value = CreateNotification.class, name = "create-notification"),
		@JsonSubTypes.Type(value = CommunityMomentStart.class, name = "active"),
		@JsonSubTypes.Type(value = DropProgress.class, name = "drop-progress"),
		@JsonSubTypes.Type(value = DropClaim.class, name = "drop-claim"),
})
@EqualsAndHashCode
@ToString
public abstract class IPubSubMessage{
}
