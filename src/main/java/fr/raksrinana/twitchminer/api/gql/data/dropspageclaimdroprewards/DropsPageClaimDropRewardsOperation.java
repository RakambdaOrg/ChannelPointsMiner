package fr.raksrinana.twitchminer.api.gql.data.dropspageclaimdroprewards;

import fr.raksrinana.twitchminer.api.gql.data.GQLOperation;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropsPageClaimDropRewardsOperation extends GQLOperation<DropsPageClaimDropRewardsData>{
	public DropsPageClaimDropRewardsOperation(@NotNull String dropInstanceId){
		super("DropsPage_ClaimDropRewards");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "2f884fa187b8fadb2a49db0adc033e636f7b6aaee6e76de1e2bba9a7baf0daf6"));
		addVariable("input", InputData.builder().dropInstanceId(dropInstanceId).build());
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<DropsPageClaimDropRewardsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
