package fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.claimcommunitypoints;

import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimCommunityPointsOperation extends IGQLOperation<ClaimCommunityPointsData>{
	public ClaimCommunityPointsOperation(@NotNull String channelId, @NotNull String claimId){
		super("ClaimCommunityPoints");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "46aaeebe02c99afdf4fc97c7c0cba964124bf6b0af229395f1f6d1feed05b3d0"));
		addVariable("input", InputData.builder().channelId(channelId).claimId(claimId).build());
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<ClaimCommunityPointsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
