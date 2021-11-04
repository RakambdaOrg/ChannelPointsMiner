package fr.raksrinana.channelpointsminer.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.channelpointsminer.cli.CLIHolder;
import fr.raksrinana.channelpointsminer.config.Configuration;
import fr.raksrinana.channelpointsminer.util.json.JacksonUtils;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import static lombok.AccessLevel.PRIVATE;

@Log4j2
@NoArgsConstructor(access = PRIVATE)
public class ConfigurationFactory{
	private static Configuration INSTANCE;
	
	/**
	 * Get the main configuration object.
	 *
	 * @return The main configuration.
	 */
	public static Configuration getInstance(){
		if(Objects.isNull(INSTANCE)){
			var configurationFile = CLIHolder.getInstance().getConfigurationFile();
			try(var fis = Files.newInputStream(configurationFile)){
				INSTANCE = JacksonUtils.read(fis, new TypeReference<>(){});
			}
			catch(IOException e){
				throw new IllegalStateException("No main config found", e);
			}
		}
		return INSTANCE;
	}
	
	protected static void resetInstance(){
		INSTANCE = null;
	}
}
