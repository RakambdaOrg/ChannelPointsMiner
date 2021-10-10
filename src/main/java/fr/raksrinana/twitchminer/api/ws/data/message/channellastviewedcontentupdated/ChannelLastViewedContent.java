package fr.raksrinana.twitchminer.api.ws.data.message.channellastviewedcontentupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.LastViewedContent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ChannelLastViewedContent{
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("channel_id")
	private String channelId;
	@JsonProperty("last_viewed_content")
	private List<LastViewedContent> lastViewedContent = new LinkedList<>();
}
