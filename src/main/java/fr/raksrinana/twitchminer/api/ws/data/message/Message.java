package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = ViewCount.class, name = "viewcount"),
		@JsonSubTypes.Type(value = GlobalLastViewedContentUpdated.class, name = "global-last-viewed-content-updated"),
		@JsonSubTypes.Type(value = ChannelLastViewedContentUpdated.class, name = "channel-last-viewed-content-updated"),
		@JsonSubTypes.Type(value = PointsEarned.class, name = "points-earned"),
		@JsonSubTypes.Type(value = ClaimAvailable.class, name = "claim-available"),
		@JsonSubTypes.Type(value = ClaimClaimed.class, name = "claim-claimed"),
		@JsonSubTypes.Type(value = StreamDown.class, name = "stream-down"),
		@JsonSubTypes.Type(value = StreamUp.class, name = "stream-up"),
		@JsonSubTypes.Type(value = EventUpdated.class, name = "event-updated"),
		@JsonSubTypes.Type(value = EventCreated.class, name = "event-created"),
		@JsonSubTypes.Type(value = Commercial.class, name = "commercial"),
		@JsonSubTypes.Type(value = RaidUpdateV2.class, name = "raid_update_v2"),
		@JsonSubTypes.Type(value = RaidGoV2.class, name = "raid_go_v2"),
		@JsonSubTypes.Type(value = PointsSpent.class, name = "points-spent"),
})
@EqualsAndHashCode
@ToString
public abstract class Message{
}
