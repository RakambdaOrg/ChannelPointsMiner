package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser.BrowserIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.http.HttpIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.manifest.ManifestVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.webpage.WebpageVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.config.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.VersionProvider;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URL;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class ApiFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private IIntegrityProvider integrityProvider;
	@Mock
	private IVersionProvider versionProvider;
	@Mock
	private BrowserConfiguration browserConfiguration;
	@Mock
	private URL url;
	
	@Test
	void createGqlApi(){
		assertThat(ApiFactory.createGqlApi(twitchLogin, integrityProvider)).isNotNull().isInstanceOf(GQLApi.class);
	}
	
	@Test
	void createTwitchApi(){
		assertThat(ApiFactory.createTwitchApi(twitchLogin)).isNotNull().isInstanceOf(TwitchApi.class);
	}
	
	@Test
	void createDiscordApi(){
		assertThat(ApiFactory.createdDiscordApi(url)).isNotNull().isInstanceOf(DiscordApi.class);
	}
	
	@Test
	void createPassportApi(){
		assertThat(ApiFactory.createPassportApi("user", "pass", Paths.get("."), false)).isNotNull().isInstanceOf(PassportApi.class);
	}
	
	@Test
	void createHttpIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(twitchLogin, versionProvider, null)).isNotNull().isInstanceOf(HttpIntegrityProvider.class);
	}
	
	@Test
	void createBrowserIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(twitchLogin, versionProvider, browserConfiguration)).isNotNull().isInstanceOf(BrowserIntegrityProvider.class);
	}
	
	@Test
	void createWebpageVersionProvider(){
		assertThat(ApiFactory.createVersionProvider(VersionProvider.WEBPAGE)).isNotNull().isInstanceOf(WebpageVersionProvider.class);
	}
	
	@Test
	void createManifestVersionProvider(){
		assertThat(ApiFactory.createVersionProvider(VersionProvider.MANIFEST)).isNotNull().isInstanceOf(ManifestVersionProvider.class);
	}
}