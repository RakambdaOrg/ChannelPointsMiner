package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.communitymomentcalloutclaim;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityMomentCalloutClaimOperation extends IGQLOperation<CommunityMomentCalloutClaimData>{
	public CommunityMomentCalloutClaimOperation(@NotNull String momentId){
		super("CommunityMomentCallout_Claim");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "e2d67415aead910f7f9ceb45a77b750a1e1d9622c936d832328a0689e054db62"));
		addVariable("input", InputData.builder().momentId(momentId).build());
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<CommunityMomentCalloutClaimData>> getResponseType(){
		return new GenericType<>(){};
	}
}
