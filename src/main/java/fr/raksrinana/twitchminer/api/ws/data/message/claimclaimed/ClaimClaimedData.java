package fr.raksrinana.twitchminer.api.ws.data.message.claimclaimed;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.api.ws.data.message.subtype.Claim;
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
public class ClaimClaimedData{
	@JsonProperty("timestamp")
	@JsonDeserialize(using = TwitchTimestampDeserializer.class)
	private Instant timestamp;
	@JsonProperty("claim")
	private Claim claim;
}
