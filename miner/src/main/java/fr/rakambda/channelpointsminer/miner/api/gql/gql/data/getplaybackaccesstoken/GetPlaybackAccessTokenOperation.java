package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.getplaybackaccesstoken;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropspageclaimdroprewards.DropsPageClaimDropRewardsData;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GetPlaybackAccessTokenOperation extends IGQLOperation<GetPlaybackAccessTokenData>{
	public GetPlaybackAccessTokenOperation(@NotNull String login){
		super("PlaybackAccessToken");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "3093517e37e4f4cb48906155bcd894150aef92617939236d2508f3375ab732ce"));
		addVariable("isLive", true);
		addVariable("login", login);
		addVariable("isVod", false);
		addVariable("vodID", "");
		addVariable("playerType", "picture-by-picture");
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<GetPlaybackAccessTokenData>> getResponseType(){
		return new GenericType<>(){};
	}
}
