package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@JsonTypeName("CommunityPointsCustomRewardGlobalCooldownSetting")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsCustomRewardGlobalCooldownSetting extends GQLType{
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("globalCooldownSeconds")
	private int globalCooldownSeconds;
}
