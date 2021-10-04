package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.TwitchTimestampDeserializer;
import lombok.Getter;
import lombok.ToString;
import java.time.Instant;

@JsonTypeName("viewcount")
@Getter
@ToString(callSuper = true)
public class ViewCountMessage extends Message{
	@JsonProperty("server_time")
	@JsonDeserialize(using = TwitchTimestampDeserializer.class)
	private Instant serverTime;
	@JsonProperty("viewers")
	private int viewers;
	
	public ViewCountMessage(){
		super("viewcount");
	}
}
