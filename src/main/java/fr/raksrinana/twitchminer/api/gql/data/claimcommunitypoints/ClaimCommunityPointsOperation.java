package fr.raksrinana.twitchminer.api.gql.data.claimcommunitypoints;

import fr.raksrinana.twitchminer.api.gql.data.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimCommunityPointsOperation extends GQLOperation<ClaimCommunityPointsData>{
	public ClaimCommunityPointsOperation(@NotNull String channelId, @NotNull String claimId){
		super("ClaimCommunityPoints");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "46aaeebe02c99afdf4fc97c7c0cba964124bf6b0af229395f1f6d1feed05b3d0"));
		addVariable("input", Map.of(
				"channelID", channelId,
				"claimID", claimId));
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<ClaimCommunityPointsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
