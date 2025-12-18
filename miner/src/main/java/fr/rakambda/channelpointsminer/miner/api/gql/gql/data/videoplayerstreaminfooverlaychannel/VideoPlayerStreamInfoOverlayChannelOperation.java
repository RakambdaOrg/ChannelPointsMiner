package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.videoplayerstreaminfooverlaychannel;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

@Getter
public class VideoPlayerStreamInfoOverlayChannelOperation extends IGQLOperation<VideoPlayerStreamInfoOverlayChannelData>{
	public VideoPlayerStreamInfoOverlayChannelOperation(@NonNull String username){
		super("VideoPlayerStreamInfoOverlayChannel");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "e785b65ff71ad7b363b34878335f27dd9372869ad0c5740a130b9268bcdbe7e7"));
		addVariable("channel", username);
	}
	
	@Override
	@NonNull
	public GenericType<GQLResponse<VideoPlayerStreamInfoOverlayChannelData>> getResponseType(){
		return new GenericType<>(){};
	}
}
