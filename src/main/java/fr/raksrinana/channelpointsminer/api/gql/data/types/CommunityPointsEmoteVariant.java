package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import java.util.List;

@JsonTypeName("CommunityPointsEmoteVariant")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsEmoteVariant extends GQLType{
	@JsonProperty("id")
	private String id;
	@JsonProperty("isUnlockable")
	private boolean unlockable;
	@JsonProperty("emote")
	private CommunityPointsEmote emote;
	@JsonProperty("modifications")
	private List<CommunityPointsEmoteModification> modifications;
}
