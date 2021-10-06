package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.api.gql.data.types.ContentId;
import fr.raksrinana.twitchminer.api.gql.data.types.ContentType;
import fr.raksrinana.twitchminer.utils.json.TwitchTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@JsonTypeName("global-last-viewed-content-updated")
@Getter
@ToString(callSuper = true)
public class GlobalLastViewedContentUpdated extends Message{
	@JsonProperty("data")
	private Data data;
	
	public GlobalLastViewedContentUpdated(){
		super("global-last-viewed-content-updated");
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Data{
		@JsonProperty("timestamp")
		@JsonDeserialize(using = TwitchTimestampDeserializer.class)
		private Instant timestamp;
		@JsonProperty("global_last_viewed_content")
		private GlobalLastViewedContent globalLastViewedContent;
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class GlobalLastViewedContent{
		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("last_viewed_content")
		private List<LastViewedContent> lastViewedContent = new LinkedList<>();
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class LastViewedContent{
		@JsonProperty("content_type")
		private ContentType contentType;
		@JsonProperty("content_id")
		private ContentId contentId;
		@JsonProperty("last_viewed_at")
		@JsonDeserialize(using = TwitchTimestampDeserializer.class)
		private Instant lastViewedAt;
	}
}
