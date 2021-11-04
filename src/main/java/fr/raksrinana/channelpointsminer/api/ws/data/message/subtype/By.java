package fr.raksrinana.channelpointsminer.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class By{
	@JsonProperty("type")
	@NotNull
	private ByType type;
	@JsonProperty("user_id")
	@NotNull
	private String userId;
	@JsonProperty("user_display_name")
	@NotNull
	private String userDisplayName;
	@JsonProperty("extension_client_id")
	@Nullable
	private String extensionClientId;
}
