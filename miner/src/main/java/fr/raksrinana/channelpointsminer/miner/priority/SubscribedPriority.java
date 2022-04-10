package fr.raksrinana.channelpointsminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.api.gql.data.types.CommunityPointsMultiplier;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.channelpointsminer.miner.api.gql.data.types.MultiplierReasonCode.SUB_T1;
import static fr.raksrinana.channelpointsminer.miner.api.gql.data.types.MultiplierReasonCode.SUB_T2;
import static fr.raksrinana.channelpointsminer.miner.api.gql.data.types.MultiplierReasonCode.SUB_T3;

@JsonTypeName("subscribed")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribedPriority extends IStreamerPriority{
	@JsonProperty("score2")
	private int score2;
	@JsonProperty("score3")
	private int score3;
	
	@Override
	public int getScore(@NotNull IMiner miner, @NotNull Streamer streamer){
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
