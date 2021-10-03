package fr.raksrinana.twitchminer.api.gql.data.response.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@JsonTypeName("CommunityPointsEmoteVariant")
@Getter
@AllArgsConstructor
public class CommunityPointsEmoteVariant extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("isUnlockable")
	private boolean unlockable;
	@JsonProperty("emote")
	private CommunityPointsEmote emote;
	@JsonProperty("modifications")
	private List<CommunityPointsEmoteModification> modifications;
	
	public CommunityPointsEmoteVariant(){
		super("CommunityPointsEmoteVariant");
	}
}
