package fr.raksrinana.twitchminer.api.ws.data.message.eventupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Outcome{
	@JsonProperty("id")
	private String id;
	@JsonProperty("color")
	private Color color;
	@JsonProperty("title")
	private String title;
	@JsonProperty("total_points")
	private long totalPoints;
	@JsonProperty("total_users")
	private int totalUsers;
	@JsonProperty("top_predictors")
	private List<Predictor> topPredictors;
	@JsonProperty("badge")
	private Badge badge;
}
