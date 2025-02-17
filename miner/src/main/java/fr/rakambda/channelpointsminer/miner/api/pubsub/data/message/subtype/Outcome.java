package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Outcome{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("color")
	@NotNull
	private OutcomeColor color;
	@JsonProperty("title")
	@NotNull
	private String title;
	@JsonProperty("total_points")
	private long totalPoints;
	@JsonProperty("total_users")
	private int totalUsers;
	@JsonProperty("top_predictors")
	@NotNull
	@Builder.Default
	private List<Predictor> topPredictors = new ArrayList<>();
	@JsonProperty("badge")
	@NotNull
	private Badge badge;
}
