package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.globallastviewedcontentupdated.GlobalLastViewedContentUpdatedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("global-last-viewed-content-updated")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GlobalLastViewedContentUpdated extends Message{
	@JsonProperty("data")
	private GlobalLastViewedContentUpdatedData data;
}
