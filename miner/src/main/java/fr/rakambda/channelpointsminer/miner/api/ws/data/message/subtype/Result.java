package fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Result{
	@JsonProperty("type")
	private ResultType type;
	@JsonProperty("points_won")
	private Integer pointsWon;
	@JsonProperty("is_acknowledged")
	private boolean isAcknowledged;
}
