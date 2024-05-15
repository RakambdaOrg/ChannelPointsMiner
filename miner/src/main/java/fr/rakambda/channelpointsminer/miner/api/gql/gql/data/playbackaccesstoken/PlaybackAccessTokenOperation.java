package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.playbackaccesstoken;

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
public class PlaybackAccessTokenOperation extends IGQLOperation<PlaybackAccessTokenData>{
	public PlaybackAccessTokenOperation(@NotNull String login){
		super("PlaybackAccessToken");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "3093517e37e4f4cb48906155bcd894150aef92617939236d2508f3375ab732ce"));
		addVariable("isLive", true);
		addVariable("isVod", false);
		addVariable("login", login);
		addVariable("playerType", "picture-by-picture");
		addVariable("vodID", "");
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<PlaybackAccessTokenData>> getResponseType(){
		return new GenericType<>(){};
	}
}
