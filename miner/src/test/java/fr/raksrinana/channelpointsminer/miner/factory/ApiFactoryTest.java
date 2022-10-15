package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser.BrowserIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.http.HttpIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.http.MobileIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.manifest.ManifestVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.webpage.WebpageVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchClient;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.browser.BrowserPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.http.HttpPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.config.VersionProvider;
import fr.raksrinana.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.login.HttpLoginMethod;
import fr.raksrinana.channelpointsminer.miner.config.login.MobileLoginMethod;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URL;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class ApiFactoryTest{
	@Mock
	private TwitchLogin twitchLogin;
	@Mock
	private TwitchLogin mobileTwitchLogin;
	@Mock
	private IIntegrityProvider integrityProvider;
	@Mock
	private IVersionProvider versionProvider;
	@Mock
	private HttpLoginMethod httpLoginMethod;
	@Mock
	private MobileLoginMethod mobileLoginMethod;
	@Mock
	private BrowserConfiguration browserConfiguration;
	@Mock
	private URL url;
	
	@BeforeEach
	void setUp(){
		lenient().when(twitchLogin.getTwitchClient()).thenReturn(TwitchClient.WEB);
		lenient().when(mobileTwitchLogin.getTwitchClient()).thenReturn(TwitchClient.MOBILE);
	}
	
	@Test
	void createGqlApi(){
		assertThat(ApiFactory.createGqlApi(twitchLogin, integrityProvider)).isNotNull().isInstanceOf(GQLApi.class);
	}
	
	@Test
	void createMobileGqlApi(){
		assertThat(ApiFactory.createGqlApi(mobileTwitchLogin, integrityProvider)).isNotNull().isInstanceOf(GQLApi.class);
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
	void createHttpPassportApi(){
		var httpLoginMethod = mock(HttpLoginMethod.class);
		when(httpLoginMethod.getAuthenticationFolder()).thenReturn(Paths.get("."));
		assertThat(ApiFactory.createPassportApi("user", httpLoginMethod)).isNotNull().isInstanceOf(HttpPassportApi.class);
	}
	
	@Test
	void createMobilepPassportApi(){
		var mobileLoginMethod = mock(MobileLoginMethod.class);
		when(mobileLoginMethod.getAuthenticationFolder()).thenReturn(Paths.get("."));
		assertThat(ApiFactory.createPassportApi("user", mobileLoginMethod)).isNotNull().isInstanceOf(HttpPassportApi.class);
	}
	
	@Test
	void createBrowserPassportApi(){
		assertThat(ApiFactory.createPassportApi("user", browserConfiguration)).isNotNull().isInstanceOf(BrowserPassportApi.class);
	}
	
	@Test
	void createHttpIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(twitchLogin, versionProvider, httpLoginMethod)).isNotNull().isInstanceOf(HttpIntegrityProvider.class);
	}
	
	@Test
	void createMobileIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(mobileTwitchLogin, versionProvider, mobileLoginMethod)).isNotNull().isInstanceOf(MobileIntegrityProvider.class);
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