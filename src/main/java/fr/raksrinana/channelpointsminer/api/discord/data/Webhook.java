package fr.raksrinana.channelpointsminer.api.discord.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.channelpointsminer.util.json.URLSerializer;
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
public class Webhook{
	@JsonProperty("username")
	private String username;
	@JsonProperty("avatar_url")
	@JsonSerialize(using = URLSerializer.class)
	private URL avatarUrl;
	@JsonProperty("content")
	private String content;
	@JsonProperty("embeds")
	private Collection<Embed> embeds;
}
