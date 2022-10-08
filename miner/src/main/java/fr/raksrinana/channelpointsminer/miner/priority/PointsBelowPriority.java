package fr.raksrinana.channelpointsminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.miner.IMiner;
import fr.raksrinana.channelpointsminer.miner.streamer.Streamer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("pointsBelow")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonClassDescription("Return a score if owned points are below a defined value.")
public class PointsBelowPriority extends IStreamerPriority{
	@JsonProperty(value = "threshold", required = true)
	@JsonPropertyDescription("Current points must strictly be below this value to give the score.")
	private int threshold;
	
	@Override
	public int getScore(@NotNull IMiner miner, @NotNull Streamer streamer){
		return streamer.getChannelPoints()
				.map(val -> val < threshold ? getScore() : 0)
				.orElse(0);
	}
}
