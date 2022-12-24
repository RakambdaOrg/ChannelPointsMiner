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
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Author{
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("url")
	@JsonSerialize(using = URLSerializer.class)
	@Nullable
	private URL url;
	@JsonProperty("icon_url")
	@JsonSerialize(using = URLSerializer.class)
	@Nullable
	private URL iconUrl;
}
