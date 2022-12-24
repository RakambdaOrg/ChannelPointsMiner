package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ColorDeserializer;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.awt.Color;
import java.time.ZonedDateTime;

@JsonTypeName("CommunityPointsCommunityGoal")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsCommunityGoal extends GQLType{
	@JsonProperty("backgroundColor")
	@JsonDeserialize(using = ColorDeserializer.class)
	private Color backgroundColor;
	@JsonProperty("description")
	private String description;
	@JsonProperty("durationDays")
	private int durationDays;
	@JsonProperty("endedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime endedAt;
	@JsonProperty("amountNeeded")
	private int amountNeeded;
	@JsonProperty("id")
	private String id;
	@JsonProperty("defaultImage")
	private CommunityPointsImage defaultImage;
	@JsonProperty("image")
	private CommunityPointsImage image;
	@JsonProperty("isInStock")
	private boolean inStock;
	@JsonProperty("smallContribution")
	private int smallContribution;
	@JsonProperty("perStreamUserMaximumContribution")
	private int perStreamUserMaximumContribution;
	@JsonProperty("pointsContributed")
	private int pointsContributed;
	@JsonProperty("startedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime startedAt;
	@JsonProperty("status")
	private CommunityPointsCommunityGoalStatus status;
	@JsonProperty("title")
	private String title;
}
