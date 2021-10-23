package fr.raksrinana.twitchminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("pointsAbove")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PointsAbovePriority extends StreamerPriority{
	@JsonProperty("threshold")
	private int threshold;
	
	@Override
	public int getScore(@NotNull Streamer streamer){
		return streamer.getChannelPoints()
				.map(val -> val > threshold ? getScore() : 0)
				.orElse(0);
	}
}
