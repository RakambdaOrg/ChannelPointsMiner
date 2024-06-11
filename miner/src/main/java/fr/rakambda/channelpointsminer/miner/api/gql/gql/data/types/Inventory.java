package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
    @JsonProperty("dropCampaignsInProgress")
    @NotNull
    @Builder.Default
    private List<DropCampaign> dropCampaignsInProgress = new ArrayList<>();    @JsonProperty("gameEventDrops")
	@NotNull
	@Builder.Default
	private List<UserDropReward> gameEventDrops = new ArrayList<>();
}
