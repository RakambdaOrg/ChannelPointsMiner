package fr.raksrinana.channelpointsminer.api.gql.data.joinraid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class InputData{
	@JsonProperty("raidID")
	private String raidId;
}
