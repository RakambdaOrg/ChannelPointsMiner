package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.api.gql.data.response.channelpointscontext.ChannelPointsContextData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

@RequiredArgsConstructor
public class Streamer{
	@NotNull
	private final String id;
	@NotNull
	@Getter
	private final String username;
	
	@Nullable
	private ChannelPointsContextData channelPointsContext;
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Streamer streamer = (Streamer) o;
		return Objects.equals(id, streamer.id);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	public void setChannelPointsContext(ChannelPointsContextData context){
		channelPointsContext = context;
	}
}
