package fr.raksrinana.twitchminer.config;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.twitchminer.cli.CLIHolder;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Log4j2
public class ConfigurationFactory{
	private static Configuration INSTANCE;
	
	/**
	 * Get the main configuration object.
	 *
	 * @return The main configuration.
	 */
	public static Configuration getInstance(){
		if(Objects.isNull(INSTANCE)){
			try(var fis = Files.newInputStream(CLIHolder.getInstance().getConfigurationFile())){
				INSTANCE = JacksonUtils.read(fis, new TypeReference<>(){});
			}
			catch(IOException e){
				log.error("Failed to read main configuration", e);
				throw new IllegalStateException("No main config found");
			}
		}
		return INSTANCE;
	}
}
