package fr.raksrinana.twitchminer.miner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Objects;

@RequiredArgsConstructor
public class Streamer{
	private final String id;
	@Getter
	private final String username;
	
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
}
