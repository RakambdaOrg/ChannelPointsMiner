package fr.raksrinana.twitchminer.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Log4j2
public class Configuration{
	@JsonIgnore
	private static Configuration INSTANCE;
	
	@NotNull
	@JsonProperty("username")
	@Comment(value = "Username of your Twitch account.")
	private String username;
	@NotNull
	@JsonProperty("password")
	@Comment(value = "Password of your Twitch account.")
	private String password;
	@JsonProperty("use2FA")
	@Comment(value = "If this account uses 2FA set this to true to directly ask for it.", defaultValue = "false")
	private boolean use2Fa = false;
	@NotNull
	@JsonProperty("authenticationFolder")
	@Comment(value = "Path to a folder that contains authentication used to log back in after a restart.", defaultValue = "./authentication")
	private Path authenticationFolder = Paths.get("authentication");
	@JsonProperty("loadFollows")
	@Comment(value = "Load streamers to scrape from follow list.", defaultValue = "false")
	private boolean loadFollows = false;
	
	public static Configuration getInstance(){
		if(Objects.isNull(INSTANCE)){
			var configurationReader = JacksonUtils.getMapper().readerFor(Configuration.class);
			
			try(var fis = Files.newInputStream(Main.getParameters().getConfigurationFile())){
				INSTANCE = configurationReader.readValue(fis);
			}
			catch(IOException e){
				log.error("Failed to read main configuration", e);
				throw new IllegalStateException("No main config found");
			}
		}
		return INSTANCE;
	}
}
