package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("CommunityPointsCustomRewardMaxPerStreamSetting")
@Getter
@AllArgsConstructor
public class CommunityPointsCustomRewardMaxPerStreamSetting extends GQLType{
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("maxPerStream")
	private int maxPerStream;
	
	public CommunityPointsCustomRewardMaxPerStreamSetting(){
		super("CommunityPointsCustomRewardMaxPerStreamSetting");
	}
}
