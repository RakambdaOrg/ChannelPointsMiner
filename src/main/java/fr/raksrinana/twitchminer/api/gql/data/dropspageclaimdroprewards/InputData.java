package fr.raksrinana.twitchminer.api.gql.data.dropspageclaimdroprewards;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class InputData{
	@JsonProperty("dropInstanceID")
	private String dropInstanceId;
}
