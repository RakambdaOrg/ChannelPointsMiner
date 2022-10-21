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
import fr.raksrinana.channelpointsminer.miner.api.passport.IPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchClient;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.api.passport.browser.BrowserPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.http.HttpPassportApi;
import fr.raksrinana.channelpointsminer.miner.api.twitch.TwitchApi;
import fr.raksrinana.channelpointsminer.miner.config.VersionProvider;
import fr.raksrinana.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.login.HttpLoginMethod;
import fr.raksrinana.channelpointsminer.miner.config.login.ILoginMethod;
import fr.raksrinana.channelpointsminer.miner.config.login.IPassportApiLoginProvider;
import fr.raksrinana.channelpointsminer.miner.config.login.MobileLoginMethod;
import fr.raksrinana.channelpointsminer.miner.log.UnirestLogger;
import fr.raksrinana.channelpointsminer.miner.util.CommonUtils;
import fr.raksrinana.channelpointsminer.miner.util.json.JacksonUtils;
import kong.unirest.core.HeaderNames;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.util.Objects;
import static kong.unirest.core.HeaderNames.USER_AGENT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ApiFactory{
	
	private static final String API_CONSUMER_TYPE_HEADER = "Api-Consumer-Type";
	private static final String X_APP_VERSION_HEADER = "X-App-Version";
	private static final String ACCEPT_MOBILE = "application/vnd.twitchtv.v3+json";
	private static final String API_CONSUMER_TYPE = "mobile; Android/1309000";
	private static final String X_APP_VERSION = "13.9.0";
	
	private static UnirestInstance createUnirestInstance(@Nullable TwitchClient twitchClient){
		var unirest = Unirest.spawnInstance();
		unirest.config()
				.enableCookieManagement(true)
				.setObjectMapper(new JacksonObjectMapper(JacksonUtils.getMapper()))
				.interceptor(new UnirestLogger());
		
		if(Objects.isNull(twitchClient) || twitchClient == TwitchClient.WEB){
			unirest.config().setDefaultHeader(USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:104.0) Gecko/20100101 Firefox/104.0");
		}
		else if(twitchClient == TwitchClient.MOBILE){
			unirest.config().setDefaultHeader(USER_AGENT, "Dalvik/2.1.0 (Linux; U; Android 7.1.2; SM-N976N Build/N2G48C) tv.twitch.android.app/13.9.0/1309000");
		}
		
		return unirest;
	}
	
	public static void addMobileHeaders(@NotNull UnirestInstance unirest){
		unirest.config()
				.setDefaultHeader(HeaderNames.ACCEPT, ACCEPT_MOBILE)
				.setDefaultHeader(API_CONSUMER_TYPE_HEADER, API_CONSUMER_TYPE)
				.setDefaultHeader(X_APP_VERSION_HEADER, X_APP_VERSION);
	}
	
	@NotNull
	public static GQLApi createGqlApi(@NotNull TwitchLogin twitchLogin, @NotNull IIntegrityProvider integrityProvider){
		var unirest = createUnirestInstance(twitchLogin.getTwitchClient());
		twitchLogin.getCookies().forEach(unirest.config()::addDefaultCookie);
		
		if(twitchLogin.getTwitchClient() == TwitchClient.MOBILE){
			addMobileHeaders(unirest);
		}
		
		return new GQLApi(twitchLogin, unirest, integrityProvider);
	}
	
	@NotNull
	public static TwitchApi createTwitchApi(@NotNull TwitchLogin twitchLogin){
		var unirest = createUnirestInstance(twitchLogin.getTwitchClient());
		twitchLogin.getCookies().forEach(unirest.config()::addDefaultCookie);
		
		return new TwitchApi(unirest);
	}
	
	@NotNull
	public static DiscordApi createdDiscordApi(@NotNull URL webhookUrl){
		var unirestInstance = createUnirestInstance(null);
		unirestInstance.config().retryAfter(true);
		
		return new DiscordApi(webhookUrl, unirestInstance);
	}
	
	@NotNull
	public static IPassportApi createPassportApi(@NotNull String username, @NotNull ILoginMethod loginMethod){
		if(loginMethod instanceof IPassportApiLoginProvider passportApiLoginProvider){
			var twitchClient = passportApiLoginProvider.getTwitchClient();
			return new HttpPassportApi(twitchClient, createUnirestInstance(twitchClient), username, passportApiLoginProvider);
		}
		if(loginMethod instanceof BrowserConfiguration browserConfiguration){
			return new BrowserPassportApi(browserConfiguration);
		}
		throw new IllegalStateException("Unknown login method");
	}
	
	@NotNull
	public static IIntegrityProvider createIntegrityProvider(@NotNull TwitchLogin twitchLogin, @NotNull IVersionProvider versionProvider, @NotNull ILoginMethod loginMethod){
		if(loginMethod instanceof HttpLoginMethod){
			return createHttpIntegrityProvider(twitchLogin, versionProvider);
		}
		if(loginMethod instanceof MobileLoginMethod){
			return createMobileIntegrityProvider(twitchLogin);
		}
		if(loginMethod instanceof BrowserConfiguration browserConfiguration){
			return createBrowserIntegrityProvider(browserConfiguration);
		}
		throw new IllegalStateException("Unknown login method");
	}
	
	@NotNull
	private static IIntegrityProvider createHttpIntegrityProvider(@NotNull TwitchLogin twitchLogin, @NotNull IVersionProvider versionProvider){
		var clientSessionId = CommonUtils.randomHex(16);
		var xDeviceId = CommonUtils.randomAlphanumeric(32);
		
		var unirest = createUnirestInstance(twitchLogin.getTwitchClient());
		twitchLogin.getCookies().forEach(unirest.config()::addDefaultCookie);
		
		return new HttpIntegrityProvider(twitchLogin, unirest, versionProvider, clientSessionId, xDeviceId);
	}
	
	@NotNull
	private static IIntegrityProvider createMobileIntegrityProvider(@NotNull TwitchLogin twitchLogin){
		var clientSessionId = CommonUtils.randomHex(16);
		var xDeviceId = CommonUtils.randomAlphanumeric(32);
		
		var unirest = createUnirestInstance(twitchLogin.getTwitchClient());
		twitchLogin.getCookies().forEach(unirest.config()::addDefaultCookie);
		addMobileHeaders(unirest);
		
		return new MobileIntegrityProvider(twitchLogin, unirest, clientSessionId, xDeviceId);
	}
	
	@NotNull
	private static IIntegrityProvider createBrowserIntegrityProvider(@NotNull BrowserConfiguration configuration){
		return new BrowserIntegrityProvider(configuration);
	}
	
	@NotNull
	public static IVersionProvider createVersionProvider(@NotNull VersionProvider versionProvider){
		var unirest = createUnirestInstance(null);
		return switch(versionProvider){
			case WEBPAGE -> new WebpageVersionProvider(unirest);
			case MANIFEST -> new ManifestVersionProvider(unirest);
		};
	}
}
