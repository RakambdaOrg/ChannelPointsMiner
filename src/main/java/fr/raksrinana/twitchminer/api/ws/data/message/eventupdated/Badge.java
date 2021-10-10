package fr.raksrinana.twitchminer.api.ws.data.message.eventupdated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Badge{
	@JsonProperty("version")
	private String version;
	@JsonProperty("set_id")
	private String setId;
}
