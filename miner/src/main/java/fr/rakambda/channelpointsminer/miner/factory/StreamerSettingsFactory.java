package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.config.AccountConfiguration;
import fr.rakambda.channelpointsminer.miner.streamer.StreamerSettings;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Log4j2
public class StreamerSettingsFactory{
	private final AccountConfiguration accountConfiguration;
	
	@NotNull
	public StreamerSettings createStreamerSettings(@NotNull String username){
		var defaultSettings = new StreamerSettings(getDefaultSettings());
		
		var streamerPathOptional = getStreamerPath(username);
		if(streamerPathOptional.isEmpty()){
			return defaultSettings;
		}
		var streamerPath = streamerPathOptional.get();
		
		try{
			if(Files.size(streamerPath) <= 0){
				log.debug("File {} is empty, using defaults", streamerPath);
				return defaultSettings;
			}
			
			try(var is = Files.newInputStream(streamerPath)){
				return JacksonUtils.update(is, defaultSettings);
			}
		}
		catch(IOException e){
			log.error("Failed to read streamer settings from {}, using defaults", streamerPath, e);
			return defaultSettings;
		}
	}
	
	@NotNull
	public StreamerSettings getDefaultSettings(){
		return accountConfiguration.getDefaultStreamerSettings();
	}
	
	@NotNull
	private Optional<Path> getStreamerPath(@NotNull String username){
		var expectedFilename = username.toLowerCase() + ".json";
		return getStreamerConfigs()
				.filter(path -> Objects.equals(path.getFileName().toString().toLowerCase(), expectedFilename))
				.findFirst();
	}
	
	@NotNull
	public Stream<Path> getStreamerConfigs(){
		return accountConfiguration.getStreamerConfigDirectories().stream()
				.flatMap(streamerDirectory -> {
					try{
						log.debug("Scanning for streamers in {} (recursive {})", streamerDirectory.getPath().toAbsolutePath(), streamerDirectory.isRecursive());
						var maxDepth = streamerDirectory.isRecursive() ? Integer.MAX_VALUE : 1;
						return Files.walk(streamerDirectory.getPath(), maxDepth);
					}
					catch(IOException e){
						log.error("Failed to search streamer settings for {}", streamerDirectory, e);
						return Stream.empty();
					}
				})
				.filter(path -> path.getFileName().toString().endsWith(".json"));
	}
}
