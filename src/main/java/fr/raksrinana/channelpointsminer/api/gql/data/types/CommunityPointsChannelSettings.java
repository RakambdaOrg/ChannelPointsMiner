package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("CommunityPointsChannelSettings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsChannelSettings extends GQLType{
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("image")
	@Nullable
	private CommunityPointsImage image;
	@JsonProperty("automaticRewards")
	@NotNull
	@Builder.Default
	private List<CommunityPointsAutomaticReward> automaticRewards = new ArrayList<>();
	@JsonProperty("customRewards")
	@NotNull
	@Builder.Default
	private List<CommunityPointsCustomReward> customRewards = new ArrayList<>();
	@JsonProperty("goals")
	@NotNull
	@Builder.Default
	private List<CommunityPointsCommunityGoal> goals = new ArrayList<>();
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("raidPointAmount")
	private long raidPointAmount;
	@JsonProperty("emoteVariants")
	@NotNull
	@Builder.Default
	private List<CommunityPointsEmoteVariant> emoteVariants = new ArrayList<>();
	@JsonProperty("earning")
	@NotNull
	private CommunityPointsChannelEarningSettings earning;
}
