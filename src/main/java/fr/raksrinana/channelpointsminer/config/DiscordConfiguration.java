package fr.raksrinana.channelpointsminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import java.net.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class DiscordConfiguration{
	@JsonProperty("webhookUrl")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL url;
	@JsonProperty("embeds")
	@Builder.Default
	private boolean embeds = false;
}
