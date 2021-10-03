package fr.raksrinana.twitchminer.api.ws.data.request.topic;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TopicName{
	COMMUNITY_POINTS_USER_V1("community-points-user-v1", true),
	PREDICTIONS_USER_V1("predictions-user-v1", true),
	VIDEO_PLAYBACK_BY_ID("video-playback-by-id", false),
	RAID("raid", false),
	PREDICTIONS_CHANNEL_V1("predictions-channel-v1", false);
	
	@Getter(onMethod_ = @JsonValue)
	private final String value;
	private final boolean ownUserTopic;
}
