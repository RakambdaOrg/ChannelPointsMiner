package fr.rakambda.channelpointsminer.miner.api.discord.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.rakambda.channelpointsminer.miner.util.json.URLSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class Webhook{
	@JsonProperty("username")
	@Setter
	private String username;
	@JsonProperty("avatar_url")
	@JsonSerialize(using = URLSerializer.class)
	private URL avatarUrl;
	@JsonProperty("content")
	private String content;
	@JsonProperty("embeds")
	private Collection<Embed> embeds;
}
