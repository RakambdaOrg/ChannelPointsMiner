package fr.rakambda.channelpointsminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.CommunityPointsMultiplier;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;
import static fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.MultiplierReasonCode.SUB_T1;
import static fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.MultiplierReasonCode.SUB_T2;
import static fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.MultiplierReasonCode.SUB_T3;

@JsonTypeName("subscribed")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonClassDescription("Return a score if the logged-in user is subscribed to the streamer.")
public class SubscribedPriority extends IStreamerPriority{
	@JsonProperty(value = "score2", required = true)
	@JsonPropertyDescription("Score for a T2 sub.")
	private int score2;
	@JsonProperty(value = "score3", required = true)
	@JsonPropertyDescription("Score for a T3 sub.")
	private int score3;
	
	@Override
	public int getScore(@NonNull IMiner miner, @NonNull Streamer streamer){
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
