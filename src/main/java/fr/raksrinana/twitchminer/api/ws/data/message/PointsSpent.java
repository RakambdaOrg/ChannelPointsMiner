package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.twitchminer.api.ws.data.message.pointsspent.PointsSpentData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@JsonTypeName("points-spent")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class PointsSpent extends Message{
	@JsonProperty("data")
	private PointsSpentData data;
}
