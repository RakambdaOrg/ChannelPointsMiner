package fr.raksrinana.twitchminer.api.gql.data.makeprediction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class InputData{
	@JsonProperty("eventID")
	private String eventId;
	@JsonProperty("outcomeID")
	private String outcomeId;
	@JsonProperty("points")
	private int points;
	@JsonProperty("transactionID")
	private String transactionId;
}
