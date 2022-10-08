package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.ColorDeserializer;
import fr.raksrinana.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.awt.Color;
import java.time.ZonedDateTime;

@JsonTypeName("CommunityPointsCustomReward")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsCustomReward extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("backgroundColor")
	@JsonDeserialize(using = ColorDeserializer.class)
	private Color backgroundColor;
	@JsonProperty("cooldownExpiresAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime cooldownExpiresAt;
	@JsonProperty("cost")
	private Integer cost;
	@JsonProperty("defaultImage")
	private CommunityPointsImage defaultImage;
	@JsonProperty("image")
	private CommunityPointsImage image;
	@JsonProperty("maxPerStreamSetting")
	private CommunityPointsCustomRewardMaxPerStreamSetting maxPerStreamSetting;
	@JsonProperty("maxPerUserPerStreamSetting")
	private CommunityPointsCustomRewardMaxPerUserPerStreamSetting maxPerUserPerStreamSetting;
	@JsonProperty("globalCooldownSetting")
	private CommunityPointsCustomRewardGlobalCooldownSetting globalCooldownSetting;
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("isInStock")
	private boolean inStock;
	@JsonProperty("isPaused")
	private boolean paused;
	@JsonProperty("isSubOnly")
	private boolean subOnly;
	@JsonProperty("isUserInputRequired")
	private boolean userInputRequired;
	@JsonProperty("shouldRedemptionsSkipRequestQueue")
	private boolean shouldRedemptionsSkipRequestQueue;
	@JsonProperty("redemptionsRedeemedCurrentStream")
	private Integer redemptionsRedeemedCurrentStream;
	@JsonProperty("prompt")
	private String prompt;
	@JsonProperty("title")
	private String title;
	@JsonProperty("updatedForIndicatorAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedForIndicatorAt;
}
