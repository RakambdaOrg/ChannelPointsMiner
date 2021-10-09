package fr.raksrinana.twitchminer.miner.data;

import fr.raksrinana.twitchminer.config.Configuration;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class StreamerSettingsFactory{
	private final Configuration configuration;
	
	@NotNull
	public StreamerSettings getDefaultSettings(){
		return configuration.getDefaultStreamerSettings();
	}
	
	@NotNull
	public StreamerSettings readStreamerSettings(){
		try{
			return getDefaultSettings().clone();
		}
		catch(CloneNotSupportedException e){
			throw new RuntimeException("Failed to read streamer settings", e);
		}
	}
}
