package fr.raksrinana.channelpointsminer.api.discord.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.channelpointsminer.util.json.InstantSerializer;
import fr.raksrinana.channelpointsminer.util.json.URLSerializer;
import lombok.*;
import java.net.URL;
import java.time.Instant;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Embed{
	@JsonProperty("author")
	private Author author;
	@JsonProperty("title")
	private String title;
	@JsonProperty("url")
	@JsonSerialize(using = URLSerializer.class)
	private URL url;
	@JsonProperty("description")
	private String description;
	@JsonProperty("color")
	private Integer color;
	@JsonProperty("fields")
	@Singular
	private Collection<Field> fields;
	@JsonProperty("thumbnail")
	private Image thumbnail;
	@JsonProperty("image")
	private Image image;
	@JsonProperty("footer")
	private Footer footer;
	@JsonProperty("timestamp")
	@JsonSerialize(using = InstantSerializer.class)
	private Instant timestamp;
}
