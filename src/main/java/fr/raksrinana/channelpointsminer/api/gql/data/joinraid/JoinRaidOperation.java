package fr.raksrinana.channelpointsminer.api.gql.data.joinraid;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class JoinRaidOperation extends IGQLOperation<JoinRaidData>{
	public JoinRaidOperation(@NotNull String raidId){
		super("JoinRaid");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae"));
		addVariable("input", InputData.builder().raidId(raidId).build());
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<JoinRaidData>> getResponseType(){
		return new GenericType<>(){};
	}
}
