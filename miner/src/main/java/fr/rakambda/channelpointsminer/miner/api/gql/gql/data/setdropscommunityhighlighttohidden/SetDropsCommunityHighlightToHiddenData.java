package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.setdropscommunityhighlighttohidden;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.SetDropsCommunityHighlightToHiddenPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class SetDropsCommunityHighlightToHiddenData{
	@JsonProperty("setDropsCommunityHighlightToHidden")
	@Nullable
	private SetDropsCommunityHighlightToHiddenPayload setDropsCommunityHighlightToHiddenPayload;
}
