package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeName("CommunityPointsCustomRewardGlobalCooldownSetting")
@Getter
@AllArgsConstructor
public class CommunityPointsCustomRewardGlobalCooldownSetting extends GQLType{
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("globalCooldownSeconds")
	private int globalCooldownSeconds;
	
	public CommunityPointsCustomRewardGlobalCooldownSetting(){
		super("CommunityPointsCustomRewardGlobalCooldownSetting");
	}
}
