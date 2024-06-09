package fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.ZonedDateTime;

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
