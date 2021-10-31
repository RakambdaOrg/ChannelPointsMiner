package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.UnknownDeserializer;
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
	@JsonProperty("drops")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object drops;
	@JsonProperty("dropCampaignsInProgress")
	@NotNull
	@Builder.Default
	private List<DropCampaign> dropCampaignsInProgress = new ArrayList<>();
	@JsonProperty("gameEventDrops")
	@JsonDeserialize(contentUsing = UnknownDeserializer.class)
	@NotNull
	@Builder.Default
	private List<Object> gameEventDrops = new ArrayList<>();
}
