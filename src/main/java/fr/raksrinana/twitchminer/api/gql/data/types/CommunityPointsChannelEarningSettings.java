package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.List;

@JsonTypeName("CommunityPointsChannelEarningSettings")
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class CommunityPointsChannelEarningSettings extends GQLType{
	@JsonProperty("id")
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
	private List<CommunityPointsWatchStreakEarningSettings> watchStreakPoints;
	@JsonProperty("multipliers")
	private List<CommunityPointsMultiplier> multipliers;
	
	public CommunityPointsChannelEarningSettings(){
		super("CommunityPointsChannelEarningSettings");
	}
}
