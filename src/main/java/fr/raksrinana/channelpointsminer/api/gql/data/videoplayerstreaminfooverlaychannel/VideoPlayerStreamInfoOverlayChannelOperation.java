package fr.raksrinana.channelpointsminer.api.gql.data.videoplayerstreaminfooverlaychannel;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.IGQLOperation;
import fr.raksrinana.channelpointsminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class VideoPlayerStreamInfoOverlayChannelOperation extends IGQLOperation<VideoPlayerStreamInfoOverlayChannelData>{
	public VideoPlayerStreamInfoOverlayChannelOperation(@NotNull String username){
		super("VideoPlayerStreamInfoOverlayChannel");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "a5f2e34d626a9f4f5c0204f910bab2194948a9502089be558bb6e779a9e1b3d2"));
		addVariable("channel", username);
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<VideoPlayerStreamInfoOverlayChannelData>> getResponseType(){
		return new GenericType<>(){};
	}
}
