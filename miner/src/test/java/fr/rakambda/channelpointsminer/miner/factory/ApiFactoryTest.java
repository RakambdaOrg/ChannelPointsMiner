package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.browser.BrowserIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.http.HttpIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.http.MobileIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.integrity.http.NoIntegrityProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.version.manifest.ManifestVersionProvider;
import fr.rakambda.channelpointsminer.miner.api.gql.version.webpage.WebpageVersionProvider;
import fr.rakambda.channelpointsminer.miner.api.hermes.TwitchHermesWebSocketPool;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchClient;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.passport.browser.BrowserLoginProvider;
import fr.rakambda.channelpointsminer.miner.api.passport.http.HttpLoginProvider;
import fr.rakambda.channelpointsminer.miner.api.passport.oauth.OauthLoginProvider;
import fr.rakambda.channelpointsminer.miner.api.telegram.TelegramApi;
import fr.rakambda.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.rakambda.channelpointsminer.miner.config.VersionProvider;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.config.login.HttpLoginMethod;
import fr.rakambda.channelpointsminer.miner.config.login.MobileLoginMethod;
import fr.rakambda.channelpointsminer.miner.config.login.TvLoginMethod;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
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
	private TwitchLogin tvTwitchLogin;
	@Mock
	private IIntegrityProvider integrityProvider;
	@Mock
	private IVersionProvider versionProvider;
	@Mock
	private HttpLoginMethod httpLoginMethod;
	@Mock
	private MobileLoginMethod mobileLoginMethod;
	@Mock
	private TvLoginMethod tvLoginMethod;
	@Mock
	private BrowserConfiguration browserConfiguration;
	@Mock
	private IEventManager eventManager;
	@Mock
	private URL url;
	
	@BeforeEach
	void setUp(){
		lenient().when(twitchLogin.getTwitchClient()).thenReturn(TwitchClient.WEB);
		lenient().when(mobileTwitchLogin.getTwitchClient()).thenReturn(TwitchClient.MOBILE);
		lenient().when(tvTwitchLogin.getTwitchClient()).thenReturn(TwitchClient.ANDROID_TV);
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
	void createTvGqlApi(){
		assertThat(ApiFactory.createGqlApi(tvTwitchLogin, integrityProvider)).isNotNull().isInstanceOf(GQLApi.class);
	}
	
	@Test
	void createTwitchApi(){
		assertThat(ApiFactory.createTwitchApi(twitchLogin)).isNotNull().isInstanceOf(TwitchApi.class);
	}
	
	@Test
	void createDiscordApi(){
		assertThat(ApiFactory.createDiscordApi(url)).isNotNull().isInstanceOf(DiscordApi.class);
	}
	
	@Test
	void createTelegramApi(){
		assertThat(ApiFactory.createTelegramApi(url)).isNotNull().isInstanceOf(TelegramApi.class);
	}
	
	@Test
	void createHttpPassportApi(){
		var httpLoginMethod = mock(HttpLoginMethod.class);
		when(httpLoginMethod.getAuthenticationFolder()).thenReturn(Paths.get("."));
		assertThat(ApiFactory.createLoginProvider("user", httpLoginMethod, eventManager)).isNotNull().isInstanceOf(HttpLoginProvider.class);
	}
	
	@Test
	void createMobilePassportApi(){
		var mobileLoginMethod = mock(MobileLoginMethod.class);
		when(mobileLoginMethod.getTwitchClient()).thenReturn(TwitchClient.MOBILE);
		
		when(mobileLoginMethod.getAuthenticationFolder()).thenReturn(Paths.get("."));
		assertThat(ApiFactory.createLoginProvider("user", mobileLoginMethod, eventManager)).isNotNull().isInstanceOf(HttpLoginProvider.class);
	}
	
	@Test
	void createTvPassportApi(){
		var mobileLoginMethod = mock(TvLoginMethod.class);
		when(mobileLoginMethod.getTwitchClient()).thenReturn(TwitchClient.ANDROID_TV);
		
		when(mobileLoginMethod.getAuthenticationFolder()).thenReturn(Paths.get("."));
		assertThat(ApiFactory.createLoginProvider("user", mobileLoginMethod, eventManager)).isNotNull().isInstanceOf(OauthLoginProvider.class);
	}
	
	@Test
	void createBrowserPassportApi(){
		assertThat(ApiFactory.createLoginProvider("user", browserConfiguration, eventManager)).isNotNull().isInstanceOf(BrowserLoginProvider.class);
	}
	
	@Test
	void createHttpIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(twitchLogin, versionProvider, httpLoginMethod, eventManager)).isNotNull().isInstanceOf(HttpIntegrityProvider.class);
	}
	
	@Test
	void createMobileIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(mobileTwitchLogin, versionProvider, mobileLoginMethod, eventManager)).isNotNull().isInstanceOf(MobileIntegrityProvider.class);
	}
	
	@Test
	void createTvIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(tvTwitchLogin, versionProvider, tvLoginMethod, eventManager)).isNotNull().isInstanceOf(NoIntegrityProvider.class);
	}
	
	@Test
	void createBrowserIntegrityProvider(){
		assertThat(ApiFactory.createIntegrityProvider(twitchLogin, versionProvider, browserConfiguration, eventManager)).isNotNull().isInstanceOf(BrowserIntegrityProvider.class);
	}
	
	@Test
	void createWebpageVersionProvider(){
		assertThat(ApiFactory.createVersionProvider(VersionProvider.WEBPAGE)).isNotNull().isInstanceOf(WebpageVersionProvider.class);
	}
	
	@Test
	void createManifestVersionProvider(){
		assertThat(ApiFactory.createVersionProvider(VersionProvider.MANIFEST)).isNotNull().isInstanceOf(ManifestVersionProvider.class);
	}
	
	@Test
	void createHermesWebSocketPool(){
		assertThat(ApiFactory.createHermesWebSocketPool(twitchLogin)).isNotNull().isInstanceOf(TwitchHermesWebSocketPool.class);
	}
}