package fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Predictor{
	@JsonProperty("points")
	private int points;
	@JsonProperty("user_display_name")
	private String userDisplayName;
}
