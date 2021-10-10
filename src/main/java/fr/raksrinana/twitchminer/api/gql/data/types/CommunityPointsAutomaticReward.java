package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ColorDeserializer;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.time.ZonedDateTime;

@JsonTypeName("CommunityPointsAutomaticReward")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsAutomaticReward extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("backgroundColor")
	@JsonDeserialize(using = ColorDeserializer.class)
	@Nullable
	private Color backgroundColor;
	@JsonProperty("cost")
	@Nullable
	private Integer cost;
	@JsonProperty("defaultBackgroundColor")
	@JsonDeserialize(using = ColorDeserializer.class)
	@NotNull
	private Color defaultBackgroundColor;
	@JsonProperty("defaultCost")
	private int defaultCost;
	@JsonProperty("defaultImage")
	@NotNull
	private CommunityPointsImage defaultImage;
	@JsonProperty("image")
	@Nullable
	private CommunityPointsImage image;
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("isHiddenForSubs")
	private boolean hiddenForSubs;
	@JsonProperty("minimumCost")
	private int minimumCost;
	@JsonProperty("type")
	@NotNull
	private RewardType type;
	@JsonProperty("updatedForIndicatorAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime updatedForIndicatorAt;
	@JsonProperty("globallyUpdatedForIndicatorAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime globallyUpdatedForIndicatorAt;
}
