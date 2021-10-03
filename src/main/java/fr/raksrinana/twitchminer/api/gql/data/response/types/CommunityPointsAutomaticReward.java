package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ColorDeserializer;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.awt.Color;
import java.time.ZonedDateTime;

@JsonTypeName("CommunityPointsAutomaticReward")
@Getter
@AllArgsConstructor
public class CommunityPointsAutomaticReward extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("backgroundColor")
	@JsonDeserialize(using = ColorDeserializer.class)
	private Color backgroundColor;
	@JsonProperty("cost")
	private Integer cost;
	@JsonProperty("defaultBackgroundColor")
	@JsonDeserialize(using = ColorDeserializer.class)
	private Color defaultBackgroundColor;
	@JsonProperty("defaultCost")
	private int defaultCost;
	@JsonProperty("defaultImage")
	private CommunityPointsImage defaultImage;
	@JsonProperty("image")
	private CommunityPointsImage image;
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("isHiddenForSubs")
	private boolean hiddenForSubs;
	@JsonProperty("minimumCost")
	private int minimumCost;
	@JsonProperty("type")
	private RewardType type;
	@JsonProperty("updatedForIndicatorAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedForIndicatorAt;
	@JsonProperty("globallyUpdatedForIndicatorAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime globallyUpdatedForIndicatorAt;
	
	public CommunityPointsAutomaticReward(){
		super("CommunityPointsAutomaticReward");
	}
}
