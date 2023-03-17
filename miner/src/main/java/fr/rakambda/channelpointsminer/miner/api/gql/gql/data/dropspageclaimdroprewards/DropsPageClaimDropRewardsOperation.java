package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropspageclaimdroprewards;

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
public class DropsPageClaimDropRewardsOperation extends IGQLOperation<DropsPageClaimDropRewardsData>{
	public DropsPageClaimDropRewardsOperation(@NotNull String dropInstanceId){
		super("DropsPage_ClaimDropRewards");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "a455deea71bdc9015b78eb49f4acfbce8baa7ccbedd28e549bb025bd0f751930"));
		addVariable("input", InputData.builder().dropInstanceId(dropInstanceId).build());
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<DropsPageClaimDropRewardsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
