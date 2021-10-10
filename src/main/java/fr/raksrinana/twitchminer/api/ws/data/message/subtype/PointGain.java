package fr.raksrinana.twitchminer.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsMultiplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PointGain{
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("channel_id")
	private String channelId;
	@JsonProperty("total_points")
	private int totalPoints;
	@JsonProperty("baseline_points")
	private int baselinePoints;
	@JsonProperty("reason_code")
	private PointReasonCode reasonCode;
	@JsonProperty("multipliers")
	private List<CommunityPointsMultiplier> multipliers;
}
