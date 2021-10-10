package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.pointsearned.PointsEarnedData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("points-earned")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class PointsEarned extends Message{
	@JsonProperty("data")
	private PointsEarnedData data;
}
