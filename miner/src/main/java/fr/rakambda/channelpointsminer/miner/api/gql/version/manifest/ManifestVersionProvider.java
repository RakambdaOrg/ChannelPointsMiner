package fr.rakambda.channelpointsminer.miner.api.gql.version.manifest;

import fr.rakambda.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.version.VersionException;
import fr.rakambda.channelpointsminer.miner.api.gql.version.manifest.data.ManifestChannel;
import fr.rakambda.channelpointsminer.miner.api.gql.version.manifest.data.ManifestRelease;
import fr.rakambda.channelpointsminer.miner.api.gql.version.manifest.data.ManifestResponse;
import kong.unirest.core.GenericType;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

@RequiredArgsConstructor
@Log4j2
public class ManifestVersionProvider implements IVersionProvider{
	private static final String LIVE_STAGE = "live";
	
	private final UnirestInstance unirest;
	
	@Override
	@NotNull
	public String getVersion() throws VersionException{
		log.info("Querying new client version");
		var response = unirest.get("https://static.twitchcdn.net/config/manifest.json")
				.queryString("v", "1")
				.asObject(new GenericType<ManifestResponse>(){});
		if(!response.isSuccess()){
			throw new VersionException(response.getStatus(), "Not success response");
		}
		
		var manifest = response.getBody();
		if(Objects.isNull(manifest)){
			throw new VersionException(response.getStatus(), "Null response");
		}
		
		var version = manifest.getChannels().stream()
				.filter(ManifestChannel::isPrimary)
				.filter(ManifestChannel::isActive)
				.map(ManifestChannel::getReleases)
				.flatMap(Collection::stream)
				.filter(release -> Objects.equals(release.getStage(), LIVE_STAGE))
				.max(Comparator.comparing(ManifestRelease::getCreated))
				.map(ManifestRelease::getBuildId);
		
		if(version.isEmpty()){
			throw new VersionException(response.getStatus(), "No version found");
		}
		
		log.info("Current client version is {}", version.get());
		return version.get();
	}
}
