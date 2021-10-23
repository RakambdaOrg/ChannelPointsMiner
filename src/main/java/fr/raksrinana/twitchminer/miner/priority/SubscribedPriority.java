package fr.raksrinana.twitchminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.gql.data.types.CommunityPointsMultiplier;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.twitchminer.api.gql.data.types.MultiplierReasonCode.*;

@JsonTypeName("subscribed")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribedPriority extends StreamerPriority{
	@JsonProperty("score2")
	private int score2;
	@JsonProperty("score3")
	private int score3;
	
	@Override
	public int getScore(@NotNull Streamer streamer){
		var reasons = streamer.getActiveMultipliers().stream()
				.map(CommunityPointsMultiplier::getReasonCode).toList();
		if(reasons.contains(SUB_T3)){
			return getScore3();
		}
		if(reasons.contains(SUB_T2)){
			return getScore2();
		}
		if(reasons.contains(SUB_T1)){
			return getScore();
		}
		return 0;
	}
}
