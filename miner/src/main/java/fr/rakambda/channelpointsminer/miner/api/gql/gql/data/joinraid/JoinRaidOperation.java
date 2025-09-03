package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.joinraid;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class JoinRaidOperation extends IGQLOperation<JoinRaidData>{
	public JoinRaidOperation(@NonNull String raidId){
		super("JoinRaid");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae"));
		addVariable("input", InputData.builder().raidId(raidId).build());
	}
	
	@Override
	@NonNull
	public GenericType<GQLResponse<JoinRaidData>> getResponseType(){
		return new GenericType<>(){};
	}
}
