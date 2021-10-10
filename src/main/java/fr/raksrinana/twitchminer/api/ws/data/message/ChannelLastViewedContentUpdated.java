package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.channellastviewedcontentupdated.ChannelLastViewedContentUpdatedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("channel-last-viewed-content-updated")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ChannelLastViewedContentUpdated extends Message{
	@JsonProperty("data")
	private ChannelLastViewedContentUpdatedData data;
}
