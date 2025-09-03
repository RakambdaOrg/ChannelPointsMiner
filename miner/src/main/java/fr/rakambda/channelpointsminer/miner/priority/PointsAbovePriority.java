package fr.rakambda.channelpointsminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;

@JsonTypeName("pointsAbove")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonClassDescription("Return a score if owned points are above a defined value.")
public class PointsAbovePriority extends IStreamerPriority{
	@JsonProperty(value = "threshold", required = true)
	@JsonPropertyDescription("Current points must strictly be above this value to give the score.")
	private int threshold;
	
	@Override
	public int getScore(@NonNull IMiner miner, @NonNull Streamer streamer){
		return streamer.getChannelPoints()
				.map(val -> val > threshold ? getScore() : 0)
				.orElse(0);
	}
}
