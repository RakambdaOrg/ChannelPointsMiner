package fr.raksrinana.twitchminer.api.ws.data.message.pointsearned;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.PointGain;
import fr.raksrinana.twitchminer.utils.json.TwitchTimestampDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PointsEarnedData{
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
