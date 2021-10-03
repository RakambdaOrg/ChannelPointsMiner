package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("CommunityPointsCustomRewardMaxPerUserPerStreamSetting")
@Getter
@AllArgsConstructor
public class CommunityPointsCustomRewardMaxPerUserPerStreamSetting extends GQLType{
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("maxPerUserPerStream")
	private int maxPerUserPerStream;
	
	public CommunityPointsCustomRewardMaxPerUserPerStreamSetting(){
		super("CommunityPointsCustomRewardMaxPerUserPerStreamSetting");
	}
}
