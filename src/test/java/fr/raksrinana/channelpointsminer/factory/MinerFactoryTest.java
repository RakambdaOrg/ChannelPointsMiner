package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.config.Configuration;
import fr.raksrinana.channelpointsminer.handler.ClaimAvailableHandler;
import fr.raksrinana.channelpointsminer.handler.FollowRaidHandler;
import fr.raksrinana.channelpointsminer.handler.PredictionsHandler;
import fr.raksrinana.channelpointsminer.handler.StreamStartEndHandler;
import fr.raksrinana.channelpointsminer.log.DiscordLoggerHandler;
import fr.raksrinana.channelpointsminer.log.LogLoggerHandler;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinerFactoryTest{
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final boolean USE_2FA = true;
	private static final Path AUTH_FOLDER = Paths.get("/path").resolve("to").resolve("auth");
	
	@Mock
	private Configuration configuration;
	@Mock
	private PassportApi passportApi;
	@Mock
	private DiscordApi discordApi;
	
	@BeforeEach
	void setUp(){
		lenient().when(configuration.getUsername()).thenReturn(USERNAME);
		lenient().when(configuration.getPassword()).thenReturn(PASSWORD);
		lenient().when(configuration.getAuthenticationFolder()).thenReturn(AUTH_FOLDER);
		lenient().when(configuration.isUse2Fa()).thenReturn(USE_2FA);
	}
	
	@Test
	void nominal(){
		try(var apiFactory = mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, PASSWORD, AUTH_FOLDER, USE_2FA)).thenReturn(passportApi);
			
			var miner = MinerFactory.create(configuration);
			
			assertThat(miner.getMessageHandlers())
					.hasSize(5)
					.hasAtLeastOneElementOfType(LogLoggerHandler.class)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithDiscord() throws MalformedURLException{
		try(var apiFactory = mockStatic(ApiFactory.class)){
			var discordWebhook = new URL("https://discord-webhook");
			
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, PASSWORD, AUTH_FOLDER, USE_2FA)).thenReturn(passportApi);
			apiFactory.when(() -> ApiFactory.createdDiscordApi(discordWebhook)).thenReturn(discordApi);
			
			when(configuration.getDiscordWebhook()).thenReturn(discordWebhook);
			
			var miner = MinerFactory.create(configuration);
			
			assertThat(miner.getMessageHandlers())
					.hasSize(6)
					.hasAtLeastOneElementOfType(LogLoggerHandler.class)
					.hasAtLeastOneElementOfType(DiscordLoggerHandler.class)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class);
			
			miner.close();
		}
	}
}