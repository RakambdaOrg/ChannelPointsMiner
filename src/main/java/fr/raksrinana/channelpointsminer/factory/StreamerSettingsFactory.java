package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.config.Configuration;
import fr.raksrinana.channelpointsminer.streamer.StreamerSettings;
import fr.raksrinana.channelpointsminer.util.json.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class StreamerSettingsFactory{
	private final Configuration configuration;
	
	@NotNull
	public StreamerSettings getDefaultSettings(){
		return configuration.getDefaultStreamerSettings();
	}
	
	@NotNull
	public StreamerSettings createStreamerSettings(@NotNull String username){
		var defaultSettings = new StreamerSettings(getDefaultSettings());
		
		var streamerPathOptional = getStreamerPath(username);
		if(streamerPathOptional.isEmpty()){
			return defaultSettings;
		}
		var streamerPath = streamerPathOptional.get();
		
		try(var is = Files.newInputStream(streamerPath)){
			return JacksonUtils.update(is, defaultSettings);
		}
		catch(IOException e){
			log.error("Failed to read streamer settings from {}, using defaults", streamerPath, e);
			return defaultSettings;
		}
	}
	
	@NotNull
	private Optional<Path> getStreamerPath(@NotNull String username){
		var expectedFilename = username.toLowerCase() + ".json";
		try{
			return Files.list(configuration.getStreamerConfigDirectory())
					.filter(path -> Objects.equals(path.getFileName().toString().toLowerCase(), expectedFilename))
					.findFirst();
		}
		catch(IOException e){
			log.error("Failed to list available streamer configurations", e);
			return Optional.empty();
		}
	}
}
