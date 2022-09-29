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
import fr.raksrinana.channelpointsminer.miner.log.UnirestLogger;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import fr.raksrinana.channelpointsminer.miner.util.json.JacksonUtils;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import static kong.unirest.core.HeaderNames.USER_AGENT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ApiFactory{
	@NotNull
	public static GQLApi createGqlApi(@NotNull TwitchLogin twitchLogin, @NotNull IIntegrityProvider integrityProvider){
		var unirest = createUnirestInstance();
		twitchLogin.getCookies().forEach(unirest.config()::addDefaultCookie);
		
		return new GQLApi(twitchLogin, unirest, integrityProvider);
	}
	
	private static UnirestInstance createUnirestInstance(){
		var unirest = Unirest.spawnInstance();
		unirest.config()
				.enableCookieManagement(true)
				.setObjectMapper(new JacksonObjectMapper(JacksonUtils.getMapper()))
				.setDefaultHeader(USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:104.0) Gecko/20100101 Firefox/104.0")
				.interceptor(new UnirestLogger());
		return unirest;
	}
	
	@NotNull
	public static TwitchApi createTwitchApi(@NotNull TwitchLogin twitchLogin){
		var unirest = createUnirestInstance();
		twitchLogin.getCookies().forEach(unirest.config()::addDefaultCookie);
		
		return new TwitchApi(unirest);
	}
	
	@NotNull
	public static DiscordApi createdDiscordApi(@NotNull URL webhookUrl){
		return new DiscordApi(webhookUrl, createUnirestInstance());
	}
	
	@NotNull
	public static PassportApi createPassportApi(@NotNull String username, @NotNull String password, @NotNull Path authenticationFolder, boolean use2Fa){
		return new PassportApi(createUnirestInstance(), username, password, authenticationFolder, use2Fa);
	}
	
	@NotNull
	public static IIntegrityProvider createIntegrityProvider(@NotNull TwitchLogin twitchLogin, @NotNull IVersionProvider versionProvider, @Nullable BrowserConfiguration configuration){
		return Objects.isNull(configuration) ? createHttpIntegrityProvider(twitchLogin, versionProvider) : createBrowserIntegrityProvider(twitchLogin, configuration);
	}
	
	@NotNull
	private static IIntegrityProvider createHttpIntegrityProvider(@NotNull TwitchLogin twitchLogin, @NotNull IVersionProvider versionProvider){
		var clientSessionId = CommonUtils.randomHex(16);
		var xDeviceId = CommonUtils.randomAlphanumeric(32);
		
		var unirest = createUnirestInstance();
		twitchLogin.getCookies().forEach(unirest.config()::addDefaultCookie);
		
		return new HttpIntegrityProvider(twitchLogin, unirest, versionProvider, clientSessionId, xDeviceId);
	}
	
	@NotNull
	private static IIntegrityProvider createBrowserIntegrityProvider(@NotNull TwitchLogin twitchLogin, @NotNull BrowserConfiguration configuration){
		return new BrowserIntegrityProvider(twitchLogin, configuration);
	}
	
	@NotNull
	public static IVersionProvider createVersionProvider(@NotNull VersionProvider versionProvider){
		var unirest = createUnirestInstance();
		return switch(versionProvider){
			case WEBPAGE -> new WebpageVersionProvider(unirest);
			case MANIFEST -> new ManifestVersionProvider(unirest);
		};
	}
}
