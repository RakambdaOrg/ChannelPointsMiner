package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.UnknownDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	@Nullable
	private Object drops;
	@JsonProperty("dropCampaignsInProgress")
	@NotNull
	@Builder.Default
	private List<DropCampaign> dropCampaignsInProgress = new ArrayList<>();
	@JsonProperty("gameEventDrops")
	@NotNull
	@Builder.Default
	private List<UserDropReward> gameEventDrops = new ArrayList<>();
}
