package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.URLDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.net.URL;

@JsonTypeName("DropBenefit")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropBenefit extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("game")
	@NotNull
	private Game game;
	@JsonProperty("imageAssetURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL imageAssetUrl;
}
