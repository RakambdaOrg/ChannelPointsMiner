package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.URLDeserializer;
import lombok.*;
import java.net.URL;

@JsonTypeName("CommunityPointsImage")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsImage extends GQLType{
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
	@JsonProperty("url2x")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url2X;
	@JsonProperty("url4x")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url4X;
}
