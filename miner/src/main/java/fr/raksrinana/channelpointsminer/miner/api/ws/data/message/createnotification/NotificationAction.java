package fr.raksrinana.channelpointsminer.miner.api.ws.data.message.createnotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.net.URL;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class NotificationAction{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("type")
	@NotNull
	private String type;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL url;
	@JsonProperty("modal_id")
	@NotNull
	private String modalId;
	@JsonProperty("body")
	@NotNull
	private String body;
	@JsonProperty("label")
	@NotNull
	private String label;
}
