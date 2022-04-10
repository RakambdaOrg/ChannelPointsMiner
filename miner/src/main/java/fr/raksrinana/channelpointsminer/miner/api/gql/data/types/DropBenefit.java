package fr.raksrinana.channelpointsminer.miner.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	@Nullable
	private Game game;
	@JsonProperty("imageAssetURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL imageAssetUrl;
}
