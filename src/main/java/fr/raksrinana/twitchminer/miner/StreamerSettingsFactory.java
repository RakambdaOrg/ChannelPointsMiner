package fr.raksrinana.twitchminer.miner;

import fr.raksrinana.twitchminer.config.Configuration;
import org.jetbrains.annotations.NotNull;

public class StreamerSettingsFactory{
	@NotNull
	public static StreamerSettings getDefaultSettings(){
		return Configuration.getInstance().getDefaultStreamerSettings();
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
