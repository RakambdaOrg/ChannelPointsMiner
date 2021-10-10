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
public class By{
	@JsonProperty("type")
	private ByType type;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("user_display_name")
	private String userDisplayName;
	@JsonProperty("extension_client_id")
	private String extensionClientId;
}
