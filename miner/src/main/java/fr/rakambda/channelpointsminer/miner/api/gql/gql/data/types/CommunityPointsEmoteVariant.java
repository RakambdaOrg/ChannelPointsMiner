package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
