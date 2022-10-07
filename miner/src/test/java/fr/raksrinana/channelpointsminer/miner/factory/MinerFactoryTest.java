package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.discord.DiscordApi;
import fr.raksrinana.channelpointsminer.miner.api.passport.http.HttpPassportApi;
import fr.raksrinana.channelpointsminer.miner.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.AnalyticsConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.DatabaseConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.DiscordConfiguration;
import fr.raksrinana.channelpointsminer.miner.config.login.ILoginMethod;
import fr.raksrinana.channelpointsminer.miner.database.DatabaseEventHandler;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.handler.ClaimAvailableHandler;
import fr.raksrinana.channelpointsminer.miner.handler.ClaimMomentHandler;
import fr.raksrinana.channelpointsminer.miner.handler.FollowRaidHandler;
import fr.raksrinana.channelpointsminer.miner.handler.PointsHandler;
import fr.raksrinana.channelpointsminer.miner.handler.PredictionsHandler;
import fr.raksrinana.channelpointsminer.miner.handler.StreamStartEndHandler;
import fr.raksrinana.channelpointsminer.miner.log.DiscordEventListener;
import fr.raksrinana.channelpointsminer.miner.log.LoggerEventListener;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class MinerFactoryTest{
	private static final String USERNAME = "username";
	
	@Mock
	private AccountConfiguration accountConfiguration;
	@Mock
	private HttpPassportApi passportApi;
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
	private DatabaseEventHandler databaseEventHandler;
	@Mock
	private ILoginMethod loginMethod;
	
	@BeforeEach
	void setUp(){
		lenient().when(accountConfiguration.getUsername()).thenReturn(USERNAME);
		lenient().when(accountConfiguration.getLoginMethod()).thenReturn(loginMethod);
		lenient().when(accountConfiguration.getDiscord()).thenReturn(discordConfiguration);
		lenient().when(accountConfiguration.getAnalytics()).thenReturn(analyticsConfiguration);
	}
	
	@Test
	void nominal(){
		try(var apiFactory = mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, loginMethod)).thenReturn(passportApi);
			
			var miner = MinerFactory.create(accountConfiguration);
			
			Assertions.assertThat(miner.getPubSubMessageHandlers())
					.hasSize(6)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class)
					.hasAtLeastOneElementOfType(ClaimMomentHandler.class);
			
			Assertions.assertThat(miner.getEventHandlers())
					.hasSize(1)
					.hasAtLeastOneElementOfType(LoggerEventListener.class);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithDiscord() throws MalformedURLException{
		try(var apiFactory = mockStatic(ApiFactory.class)){
			var discordWebhook = new URL("https://discord-webhook");
			
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, loginMethod)).thenReturn(passportApi);
			apiFactory.when(() -> ApiFactory.createdDiscordApi(discordWebhook)).thenReturn(discordApi);
			
			when(discordConfiguration.getUrl()).thenReturn(discordWebhook);
			
			var miner = MinerFactory.create(accountConfiguration);
			
			Assertions.assertThat(miner.getPubSubMessageHandlers())
					.hasSize(6)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class)
					.hasAtLeastOneElementOfType(ClaimMomentHandler.class);
			
			Assertions.assertThat(miner.getEventHandlers())
					.hasSize(2)
					.hasAtLeastOneElementOfType(LoggerEventListener.class)
					.hasAtLeastOneElementOfType(DiscordEventListener.class);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithAnalytics() throws SQLException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var databaseFactory = mockStatic(DatabaseFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, loginMethod)).thenReturn(passportApi);
			databaseFactory.when(() -> DatabaseFactory.createDatabase(databaseConfiguration)).thenReturn(database);
			databaseFactory.when(() -> DatabaseFactory.createDatabaseHandler(database)).thenReturn(databaseEventHandler);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			when(analyticsConfiguration.getDatabase()).thenReturn(databaseConfiguration);
			
			var miner = MinerFactory.create(accountConfiguration);
			
			Assertions.assertThat(miner.getPubSubMessageHandlers())
					.hasSize(6)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class)
					.hasAtLeastOneElementOfType(ClaimMomentHandler.class);
			
			Assertions.assertThat(miner.getEventHandlers())
					.hasSize(2)
					.hasAtLeastOneElementOfType(LoggerEventListener.class)
					.hasAtLeastOneElementOfType(DatabaseEventHandler.class);
			
			verify(database).deleteAllUserPredictions();
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithAnalyticsException(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var databaseFactory = mockStatic(DatabaseFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, loginMethod)).thenReturn(passportApi);
			databaseFactory.when(() -> DatabaseFactory.createDatabase(null)).thenThrow(new SQLException("For tests"));
			
			assertThrows(IllegalStateException.class, () -> MinerFactory.create(accountConfiguration));
		}
	}
	
	@Test
	void nominalWithAnalyticsButNoDatabase(){
		try(var apiFactory = mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.createPassportApi(USERNAME, loginMethod)).thenReturn(passportApi);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			when(analyticsConfiguration.getDatabase()).thenReturn(null);
			
			assertThrows(IllegalStateException.class, () -> MinerFactory.create(accountConfiguration));
		}
	}
}