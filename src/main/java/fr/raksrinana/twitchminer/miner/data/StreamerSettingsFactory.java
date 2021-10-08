package fr.raksrinana.twitchminer.miner.data;

import fr.raksrinana.twitchminer.config.ConfigurationFactory;
import org.jetbrains.annotations.NotNull;

public class StreamerSettingsFactory{
	@NotNull
	public static StreamerSettings getDefaultSettings(){
		return ConfigurationFactory.getInstance().getDefaultStreamerSettings();
	}
	
	@NotNull
	public static StreamerSettings readStreamerSettings(){
		try{
			return getDefaultSettings().clone();
		}
		catch(Exception e){
			throw new RuntimeException("Failed to read streamer settings", e);
		}
	}
}
