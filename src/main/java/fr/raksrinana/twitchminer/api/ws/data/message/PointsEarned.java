package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.PointGain;
import fr.raksrinana.twitchminer.utils.json.TwitchTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;

@JsonTypeName("points-earned")
@Getter
@ToString(callSuper = true)
public class PointsEarned extends Message{
	@JsonProperty("data")
	private Data data;
	
	public PointsEarned(){
		super("points-earned");
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Data{
		@JsonProperty("timestamp")
		@JsonDeserialize(using = TwitchTimestampDeserializer.class)
		private Instant timestamp;
		@JsonProperty("channel_id")
		private String channelId;
		@JsonProperty("point_gain")
		private PointGain pointGain;
		@JsonProperty("balance")
		private Balance balance;
	}
	
	@Getter
	@NoArgsConstructor
	@ToString
	static class Balance{
		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("channel_id")
		private String channelId;
		@JsonProperty("balance")
		private int balance;
	}
}
