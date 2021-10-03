package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("CommunityPointsWatchStreakEarningSettings")
@Getter
@AllArgsConstructor
public class CommunityPointsWatchStreakEarningSettings extends GQLType{
	@JsonProperty("points")
	private int points;
	
	public CommunityPointsWatchStreakEarningSettings(){
		super("CommunityPointsWatchStreakEarningSettings");
	}
}
