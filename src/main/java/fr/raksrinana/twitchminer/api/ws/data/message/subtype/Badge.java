package fr.raksrinana.twitchminer.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Badge{
	@JsonProperty("version")
	@NotNull
	private String version;
	@JsonProperty("set_id")
	@NotNull
	private String setId;
}
