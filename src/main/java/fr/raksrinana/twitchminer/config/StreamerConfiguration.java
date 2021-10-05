package fr.raksrinana.twitchminer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.miner.StreamerSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
public class StreamerConfiguration{
	@JsonProperty("username")
	@Comment("Username of the streamer")
	@NotNull
	private String username;
	@JsonProperty("settings")
	@Comment("Custom settings for this streamer (values defined here overrides the default config)")
	@Nullable
	private StreamerSettings settings;
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		
		StreamerConfiguration that = (StreamerConfiguration) o;
		return username.equalsIgnoreCase(that.username);
	}
	
	@Override
	public int hashCode(){
		return username.toLowerCase().hashCode();
	}
}
