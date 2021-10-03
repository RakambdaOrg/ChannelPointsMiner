package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.ZonedDateTime;

@JsonTypeName("CommunityPointsEmoteModification")
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class CommunityPointsEmoteModification extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("emote")
	private CommunityPointsEmote emote;
	@JsonProperty("modifierIconDark")
	private CommunityPointsImage modifierIconDark;
	@JsonProperty("modifierIconLight")
	private CommunityPointsImage modifierIconLight;
	@JsonProperty("title")
	private String title;
	@JsonProperty("globallyUpdatedForIndicatorAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime globallyUpdatedForIndicatorAt;
	
	public CommunityPointsEmoteModification(){
		super("CommunityPointsEmoteModification");
	}
}
