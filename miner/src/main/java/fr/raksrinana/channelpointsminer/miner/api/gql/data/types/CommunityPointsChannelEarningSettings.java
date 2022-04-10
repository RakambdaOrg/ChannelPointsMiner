package fr.raksrinana.channelpointsminer.miner.api.gql.data.types;

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

@JsonTypeName("CommunityPointsChannelEarningSettings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsChannelEarningSettings extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("averagePointsPerHour")
	private int averagePointsPerHour;
	@JsonProperty("cheerPoints")
	private int cheerPoints;
	@JsonProperty("claimPoints")
	private int claimPoints;
	@JsonProperty("followPoints")
	private int followPoints;
	@JsonProperty("passiveWatchPoints")
	private int passiveWatchPoints;
	@JsonProperty("raidPoints")
	private int raidPoints;
	@JsonProperty("subscriptionGiftPoints")
	private int subscriptionGiftPoints;
	@JsonProperty("watchStreakPoints")
	@NotNull
	@Builder.Default
	private List<CommunityPointsWatchStreakEarningSettings> watchStreakPoints = new ArrayList<>();
	@JsonProperty("multipliers")
	@NotNull
	@Builder.Default
	private List<CommunityPointsMultiplier> multipliers = new ArrayList<>();
}
