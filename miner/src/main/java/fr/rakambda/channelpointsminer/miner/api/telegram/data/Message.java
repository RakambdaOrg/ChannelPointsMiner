package fr.rakambda.channelpointsminer.miner.api.telegram.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.rakambda.channelpointsminer.miner.api.discord.data.Embed;
import fr.rakambda.channelpointsminer.miner.util.json.URLSerializer;
import lombok.*;
import java.net.URL;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Message {
	@JsonProperty("chat_id")
	private String chatId;
	@JsonProperty("text")
	private String text;
	@JsonProperty("parse_mode")
	private String parseMode;
}
