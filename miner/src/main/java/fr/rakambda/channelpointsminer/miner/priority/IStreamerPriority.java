package fr.rakambda.channelpointsminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@Type(value = ConstantPriority.class, name = "constant"),
		@Type(value = SubscribedPriority.class, name = "subscribed"),
		@Type(value = PointsAbovePriority.class, name = "pointsAbove"),
		@Type(value = PointsBelowPriority.class, name = "pointsBelow"),
		@Type(value = WatchStreakPriority.class, name = "watchStreak"),
		@Type(value = DropsPriority.class, name = "drops"),
})
@ToString
@EqualsAndHashCode
@SuperBuilder
@JsonClassDescription("Priorities is a way to prioritize streamers among each others to mine one over another based on some conditions.")
public abstract class IStreamerPriority{
	@JsonProperty(value = "score", required = true)
	@JsonPropertyDescription("Score to give.")
	private int score;
	
	public abstract int getScore(@NonNull IMiner miner, @NonNull Streamer streamer);
	
	public boolean isDropsRelated(){
		return false;
	}
}
