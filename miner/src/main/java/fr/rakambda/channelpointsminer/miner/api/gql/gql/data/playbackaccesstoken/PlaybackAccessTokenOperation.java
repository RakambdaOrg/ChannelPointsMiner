package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.playbackaccesstoken;

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
public class PlaybackAccessTokenOperation extends IGQLOperation<PlaybackAccessTokenData>{
	public PlaybackAccessTokenOperation(@NonNull String login){
		super("PlaybackAccessToken");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "ed230aa1e33e07eebb8928504583da78a5173989fadfb1ac94be06a04f3cdbe9"));
		addVariable("isLive", true);
		addVariable("isVod", false);
		addVariable("login", login);
		addVariable("platform", "web");
		addVariable("playerType", "picture-by-picture");
		addVariable("vodID", "");
	}
	
	@Override
	@NonNull
	public GenericType<GQLResponse<PlaybackAccessTokenData>> getResponseType(){
		return new GenericType<>(){};
	}
}
