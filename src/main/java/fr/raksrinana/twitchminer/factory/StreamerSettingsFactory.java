package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.config.Configuration;
import fr.raksrinana.twitchminer.miner.data.StreamerSettings;
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
	public StreamerSettings createStreamerSettings(@NotNull String username){
		try{
			return getDefaultSettings().clone();
		}
		catch(CloneNotSupportedException e){
			throw new RuntimeException("Failed to read streamer settings", e);
		}
	}
}
