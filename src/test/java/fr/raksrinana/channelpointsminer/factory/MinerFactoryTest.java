package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.api.passport.PassportApi;
import fr.raksrinana.channelpointsminer.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.config.AnalyticsConfiguration;
import fr.raksrinana.channelpointsminer.config.DatabaseConfiguration;
import fr.raksrinana.channelpointsminer.config.DiscordConfiguration;
import fr.raksrinana.channelpointsminer.database.DatabaseHandler;
import fr.raksrinana.channelpointsminer.database.IDatabase;
import fr.raksrinana.channelpointsminer.handler.ClaimAvailableHandler;
import fr.raksrinana.channelpointsminer.handler.FollowRaidHandler;
import fr.raksrinana.channelpointsminer.handler.PointsHandler;
import fr.raksrinana.channelpointsminer.handler.PredictionsHandler;
import fr.raksrinana.channelpointsminer.handler.StreamStartEndHandler;
import fr.raksrinana.channelpointsminer.log.DiscordEventListener;
import fr.raksrinana.channelpointsminer.log.LoggerEventListener;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinerFactoryTest{
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final boolean USE_2FA = true;
	private static final Path AUTH_FOLDER = Paths.get("/path").resolve("to").resolve("auth");
	
	@Mock
	private AccountConfiguration accountConfiguration;
	@Mock
	private PassportApi passportApi;
	@Mock
	private DiscordApi discordApi;
	@Mock
	private DiscordConfiguration discordConfiguration;
	@Mock
	private AnalyticsConfiguration analyticsConfiguration;
	@Mock
	private DatabaseConfiguration databaseConfiguration;
	@Mock
	private IDatabase database;
	@Mock
	private DatabaseHandler databaseHandler;
	
	@BeforeEach
	void setUp(){
		lenient().when(accountConfiguration.getUsername()).thenReturn(USERNAME);
		lenient().when(accountConfiguration.getPassword()).thenReturn(PASSWORD);
		lenient().when(accountConfiguration.getAuthenticationFolder()).thenReturn(AUTH_FOLDER);
		lenient().when(accountConfiguration.isUse2Fa()).thenReturn(USE_2FA);
		lenient().when(accountConfiguration.getDiscord()).thenReturn(discordConfiguration);
		lenient().when(accountConfiguration.getAnalytics()).thenReturn(analyticsConfiguration);
		lenient().when(analyticsConfiguration.getDatabase()).thenReturn(databaseConfiguration);
	}
	
	@Test
	void nominal(){
		try(var apiFactory = mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, PASSWORD, AUTH_FOLDER, USE_2FA)).thenReturn(passportApi);
			
			var miner = MinerFactory.create(accountConfiguration);
			
			assertThat(miner.getMessageHandlers())
					.hasSize(5)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class);
			
			assertThat(miner.getEventListeners())
					.hasSize(1)
					.hasAtLeastOneElementOfType(LoggerEventListener.class);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithDiscord() throws MalformedURLException{
		try(var apiFactory = mockStatic(ApiFactory.class)){
			var discordWebhook = new URL("https://discord-webhook");
			
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, PASSWORD, AUTH_FOLDER, USE_2FA)).thenReturn(passportApi);
			apiFactory.when(() -> ApiFactory.createdDiscordApi(discordWebhook)).thenReturn(discordApi);
			
			when(discordConfiguration.getUrl()).thenReturn(discordWebhook);
			
			var miner = MinerFactory.create(accountConfiguration);
			
			assertThat(miner.getMessageHandlers())
					.hasSize(5)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class);
			
			assertThat(miner.getEventListeners())
					.hasSize(2)
					.hasAtLeastOneElementOfType(LoggerEventListener.class)
					.hasAtLeastOneElementOfType(DiscordEventListener.class);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithAnalytics(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var databaseFactory = mockStatic(DatabaseFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, PASSWORD, AUTH_FOLDER, USE_2FA)).thenReturn(passportApi);
			databaseFactory.when(() -> DatabaseFactory.createDatabase(databaseConfiguration)).thenReturn(database);
			databaseFactory.when(() -> DatabaseFactory.createDatabaseHandler(database)).thenReturn(databaseHandler);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			
			var miner = MinerFactory.create(accountConfiguration);
			
			assertThat(miner.getMessageHandlers())
					.hasSize(5)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class);
			
			assertThat(miner.getEventListeners())
					.hasSize(2)
					.hasAtLeastOneElementOfType(LoggerEventListener.class)
					.hasAtLeastOneElementOfType(DatabaseHandler.class);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithAnalyticsException(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var databaseFactory = mockStatic(DatabaseFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, PASSWORD, AUTH_FOLDER, USE_2FA)).thenReturn(passportApi);
			databaseFactory.when(() -> DatabaseFactory.createDatabase(databaseConfiguration)).thenThrow(new SQLException("For tests"));
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			
			assertThrows(IllegalStateException.class, () -> MinerFactory.create(accountConfiguration));
		}
	}
	
	@Test
	void nominalWithAnalyticsButNoDatabase(){
		try(var apiFactory = mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, PASSWORD, AUTH_FOLDER, USE_2FA)).thenReturn(passportApi);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			when(analyticsConfiguration.getDatabase()).thenReturn(null);
			
			assertThrows(IllegalStateException.class, () -> MinerFactory.create(accountConfiguration));
		}
	}
}