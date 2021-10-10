package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.UnknownDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("Inventory")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class Inventory extends GQLType{
	@JsonProperty("gameEventDrops")
	@JsonDeserialize(contentUsing = UnknownDeserializer.class)
	@NotNull
	@Builder.Default
	private List<Object> gameEventDrops = new ArrayList<>();
}
