package fr.rakambda.channelpointsminer.miner.api.discord.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Embed{
	@JsonProperty("author")
	private Author author;
	@JsonProperty("description")
	private String description;
	@JsonProperty("color")
	private Integer color;
	@JsonProperty("fields")
	@Singular
	private Collection<Field> fields;
	@JsonProperty("footer")
	private Footer footer;
}
