package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.api.gql.data.types.ContentType;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.twitchminer.utils.json.TwitchTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@JsonTypeName("channel-last-viewed-content-updated")
@Getter
@ToString(callSuper = true)
public class ChannelLastViewedContentUpdated extends Message{
	@JsonProperty("data")
	private Data data;
	
	public ChannelLastViewedContentUpdated(){
		super("channel-last-viewed-content-updated");
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Data{
		@JsonProperty("timestamp")
		@JsonDeserialize(using = TwitchTimestampDeserializer.class)
		private Instant timestamp;
		@JsonProperty("channel_last_viewed_content")
		private ChannelLastViewedContent channelLastViewedContent;
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class ChannelLastViewedContent{
		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("channel_id")
		private String channelId;
		@JsonProperty("last_viewed_content")
		private List<LastViewedContent> lastViewedContent = new LinkedList<>();
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class LastViewedContent{
		@JsonProperty("content_type")
		private ContentType contentType;
		@JsonProperty("last_viewed_at")
		@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
		private Instant lastViewedAt;
	}
}
