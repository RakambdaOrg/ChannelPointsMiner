package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.gql.GQLApi;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.IIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser.BrowserIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.http.HttpIntegrityProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.IVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.manifest.ManifestVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.gql.version.webpage.WebpageVersionProvider;
import fr.raksrinana.channelpointsminer.miner.api.passport.IPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.browser.BrowserPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.http.HttpPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.config.VersionProvider;
import fr.raksrinana.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.login.HttpLoginMethod;
import fr.raksrinana.channelpointsminer.miner.config.login.ILoginMethod;
import fr.raksrinana.channelpointsminer.miner.log.UnirestLogger;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import fr.raksrinana.channelpointsminer.miner.util.json.JacksonUtils;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
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
	public static IPassportApi createPassportApi(@NotNull String username, @NotNull ILoginMethod loginMethod){
		if(loginMethod instanceof HttpLoginMethod httpLoginMethod){
			return new HttpPassportApi(createUnirestInstance(), username, httpLoginMethod);
		}
		if(loginMethod instanceof BrowserConfiguration browserConfiguration){
			return new BrowserPassportApi(browserConfiguration);
		}
		throw new IllegalStateException("Unknown login method");
	}
	
	@NotNull
	public static IIntegrityProvider createIntegrityProvider(@NotNull TwitchLogin twitchLogin, @NotNull IVersionProvider versionProvider, @NotNull ILoginMethod loginMethod){
		if(loginMethod instanceof BrowserConfiguration browserConfiguration){
			return createBrowserIntegrityProvider(browserConfiguration);
		}
		return createHttpIntegrityProvider(twitchLogin, versionProvider);
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
	private static IIntegrityProvider createBrowserIntegrityProvider(@NotNull BrowserConfiguration configuration){
		return new BrowserIntegrityProvider(configuration);
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
