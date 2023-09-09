package fr.rakambda.channelpointsminer.miner.api.ws.data.request.topic;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum TopicName{
	COMMUNITY_POINTS_USER_V1("community-points-user-v1", true),
	PREDICTIONS_USER_V1("predictions-user-v1", true),
	VIDEO_PLAYBACK_BY_ID("video-playback-by-id", false),
	RAID("raid", false),
	PREDICTIONS_CHANNEL_V1("predictions-channel-v1", false),
	ONSITE_NOTIFICATIONS("onsite-notifications", true),
	COMMUNITY_MOMENTS_CHANNEL_V1("community-moments-channel-v1", false),
	USER_DROP_EVENTS("user-drop-events", false);
	
	@Getter(onMethod_ = @JsonValue)
	private final String value;
	private final boolean ownUserTopic;
	
	public static Optional<TopicName> fromValue(String value){
		for(var topic : TopicName.values()){
			if(Objects.equals(value, topic.getValue())){
				return Optional.of(topic);
			}
		}
		return Optional.empty();
	}
}
