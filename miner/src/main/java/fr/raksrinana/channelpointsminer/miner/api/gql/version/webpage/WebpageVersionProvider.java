package fr.raksrinana.channelpointsminer.miner.api.gql.version.webpage;

import fr.raksrinana.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.VersionException;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Log4j2
public class WebpageVersionProvider implements IVersionProvider{
	private static final Pattern TWILIGHT_BUILD_ID_PATTERN = Pattern.compile("window\\.__twilightBuildID=\"([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-4[0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12})\";");
	
	private final UnirestInstance unirest;
	
	@Override
	@NotNull
	public String getVersion() throws VersionException{
		log.info("Querying new client version");
		var response = unirest.get("https://www.twitch.tv").asString();
		if(!response.isSuccess()){
			throw new VersionException(response.getStatus(), "Not success response");
		}
		
		var page = response.getBody();
		if(Objects.isNull(page)){
			throw new VersionException(response.getStatus(), "Null page");
		}
		
		var matcher = TWILIGHT_BUILD_ID_PATTERN.matcher(page);
		if(!matcher.find()){
			throw new VersionException(response.getStatus(), "No version found");
		}
		
		var version = matcher.group(1);
		log.info("Current client version is {}", version);
		return version;
	}
}
