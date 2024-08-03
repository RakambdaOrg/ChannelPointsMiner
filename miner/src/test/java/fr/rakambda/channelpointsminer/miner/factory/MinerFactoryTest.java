package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.api.discord.DiscordApi;
import fr.rakambda.channelpointsminer.miner.api.passport.http.HttpLoginProvider;
import fr.rakambda.channelpointsminer.miner.api.telegram.TelegramApi;
import fr.rakambda.channelpointsminer.miner.config.AccountConfiguration;
import fr.rakambda.channelpointsminer.miner.config.AnalyticsConfiguration;
import fr.rakambda.channelpointsminer.miner.config.DatabaseConfiguration;
import fr.rakambda.channelpointsminer.miner.config.DiscordConfiguration;
import fr.rakambda.channelpointsminer.miner.config.TelegramConfiguration;
import fr.rakambda.channelpointsminer.miner.config.login.ILoginMethod;
import fr.rakambda.channelpointsminer.miner.database.DatabaseEventHandler;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.handler.ClaimAvailableHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimDropHandler;
import fr.rakambda.channelpointsminer.miner.handler.ClaimMomentHandler;
import fr.rakambda.channelpointsminer.miner.handler.FollowRaidHandler;
import fr.rakambda.channelpointsminer.miner.handler.NotificationHandler;
import fr.rakambda.channelpointsminer.miner.handler.PointsHandler;
import fr.rakambda.channelpointsminer.miner.handler.PredictionsHandler;
import fr.rakambda.channelpointsminer.miner.handler.StreamStartEndHandler;
import fr.rakambda.channelpointsminer.miner.log.LoggerEventListener;
import fr.rakambda.channelpointsminer.miner.log.discord.DiscordEventListener;
import fr.rakambda.channelpointsminer.miner.log.telegram.TelegramEventListener;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.runnable.SyncInventory;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class MinerFactoryTest{
	private static final String USERNAME = "username";
	private static final boolean RECORD_USER_PREDICTIONS = false;
	
	@Mock
	private AccountConfiguration accountConfiguration;
	@Mock
	private HttpLoginProvider passportApi;
	@Mock
	private DiscordApi discordApi;
	@Mock
	private TelegramApi telegramApi;
	@Mock
	private DiscordConfiguration discordConfiguration;
	@Mock
	private TelegramConfiguration telegramConfiguration;
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
	@Mock
	private IEventManager eventManager;
	@Mock
	private SyncInventory syncInventory;
	
	@BeforeEach
	void setUp(){
		lenient().when(accountConfiguration.getUsername()).thenReturn(USERNAME);
		lenient().when(accountConfiguration.getLoginMethod()).thenReturn(loginMethod);
		lenient().when(accountConfiguration.getDiscord()).thenReturn(discordConfiguration);
		lenient().when(accountConfiguration.getTelegram()).thenReturn(telegramConfiguration);
		lenient().when(accountConfiguration.getAnalytics()).thenReturn(analyticsConfiguration);
		lenient().when(analyticsConfiguration.isRecordUserPredictions()).thenReturn(RECORD_USER_PREDICTIONS);
	}
	
	@Test
	void nominal(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var minerRunnableFactory = mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(() -> ApiFactory.createLoginProvider(USERNAME, loginMethod, eventManager)).thenReturn(passportApi);
			minerRunnableFactory.when(() -> MinerRunnableFactory.createSyncInventory(any(IMiner.class), eq(eventManager))).thenReturn(syncInventory);
			
			var miner = MinerFactory.create(accountConfiguration, eventManager);
			
			assertThat(miner.getSyncInventory()).isEqualTo(syncInventory);
			assertThat(miner.getPubSubMessageHandlers())
					.hasSize(8)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class)
					.hasAtLeastOneElementOfType(ClaimMomentHandler.class)
					.hasAtLeastOneElementOfType(ClaimDropHandler.class)
					.hasAtLeastOneElementOfType(NotificationHandler.class);
			
			verify(eventManager).addEventHandler(any(LoggerEventListener.class));
			verifyNoMoreInteractions(eventManager);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithDiscord() throws MalformedURLException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var minerRunnableFactory = mockStatic(MinerRunnableFactory.class)){
			var discordWebhook = new URL("https://discord-webhook");
			
			apiFactory.when(() -> ApiFactory.createLoginProvider(USERNAME, loginMethod, eventManager)).thenReturn(passportApi);
			apiFactory.when(() -> ApiFactory.createDiscordApi(discordWebhook)).thenReturn(discordApi);
			minerRunnableFactory.when(() -> MinerRunnableFactory.createSyncInventory(any(IMiner.class), eq(eventManager))).thenReturn(syncInventory);
			
			when(discordConfiguration.getUrl()).thenReturn(discordWebhook);
			
			var miner = MinerFactory.create(accountConfiguration, eventManager);
			
			assertThat(miner.getSyncInventory()).isEqualTo(syncInventory);
			assertThat(miner.getPubSubMessageHandlers())
					.hasSize(8)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class)
					.hasAtLeastOneElementOfType(ClaimMomentHandler.class)
					.hasAtLeastOneElementOfType(ClaimDropHandler.class)
					.hasAtLeastOneElementOfType(NotificationHandler.class);
			
			verify(eventManager).addEventHandler(any(LoggerEventListener.class));
			verify(eventManager).addEventHandler(any(DiscordEventListener.class));
			verifyNoMoreInteractions(eventManager);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithTelegram() throws MalformedURLException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var minerRunnableFactory = mockStatic(MinerRunnableFactory.class)){
			var botUrl = new URL("https://api.telegram.org/bot123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11");
			
			apiFactory.when(() -> ApiFactory.createLoginProvider(USERNAME, loginMethod, eventManager)).thenReturn(passportApi);
			apiFactory.when(() -> ApiFactory.createTelegramApi(botUrl)).thenReturn(telegramApi);
			minerRunnableFactory.when(() -> MinerRunnableFactory.createSyncInventory(any(IMiner.class), eq(eventManager))).thenReturn(syncInventory);
			
			when(telegramConfiguration.getToken()).thenReturn("123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11");
			
			var miner = MinerFactory.create(accountConfiguration, eventManager);
			
			assertThat(miner.getSyncInventory()).isEqualTo(syncInventory);
			assertThat(miner.getPubSubMessageHandlers())
					.hasSize(8)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class)
					.hasAtLeastOneElementOfType(ClaimMomentHandler.class)
					.hasAtLeastOneElementOfType(ClaimDropHandler.class)
					.hasAtLeastOneElementOfType(NotificationHandler.class);
			
			verify(eventManager).addEventHandler(any(LoggerEventListener.class));
			verify(eventManager).addEventHandler(any(TelegramEventListener.class));
			verifyNoMoreInteractions(eventManager);
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithAnalytics() throws SQLException{
		try(var apiFactory = mockStatic(ApiFactory.class);
				var databaseFactory = mockStatic(DatabaseFactory.class);
				var minerRunnableFactory = mockStatic(MinerRunnableFactory.class)){
			apiFactory.when(() -> ApiFactory.createLoginProvider(USERNAME, loginMethod, eventManager)).thenReturn(passportApi);
			databaseFactory.when(() -> DatabaseFactory.createDatabase(databaseConfiguration)).thenReturn(database);
			databaseFactory.when(() -> DatabaseFactory.createDatabaseHandler(database, RECORD_USER_PREDICTIONS)).thenReturn(databaseEventHandler);
			minerRunnableFactory.when(() -> MinerRunnableFactory.createSyncInventory(any(IMiner.class), eq(eventManager))).thenReturn(syncInventory);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			when(analyticsConfiguration.getDatabase()).thenReturn(databaseConfiguration);
			
			var miner = MinerFactory.create(accountConfiguration, eventManager);
			
			assertThat(miner.getSyncInventory()).isEqualTo(syncInventory);
			assertThat(miner.getPubSubMessageHandlers())
					.hasSize(8)
					.hasAtLeastOneElementOfType(ClaimAvailableHandler.class)
					.hasAtLeastOneElementOfType(StreamStartEndHandler.class)
					.hasAtLeastOneElementOfType(FollowRaidHandler.class)
					.hasAtLeastOneElementOfType(PredictionsHandler.class)
					.hasAtLeastOneElementOfType(PointsHandler.class)
					.hasAtLeastOneElementOfType(ClaimMomentHandler.class)
					.hasAtLeastOneElementOfType(ClaimDropHandler.class)
					.hasAtLeastOneElementOfType(NotificationHandler.class);
			
			verify(eventManager).addEventHandler(any(LoggerEventListener.class));
			verify(eventManager).addEventHandler(any(DatabaseEventHandler.class));
			verifyNoMoreInteractions(eventManager);
			
			verify(database).deleteAllUserPredictions();
			
			miner.close();
		}
	}
	
	@Test
	void nominalWithAnalyticsException(){
		try(var apiFactory = mockStatic(ApiFactory.class);
				var databaseFactory = mockStatic(DatabaseFactory.class)){
			apiFactory.when(() -> ApiFactory.createLoginProvider(USERNAME, loginMethod, eventManager)).thenReturn(passportApi);
			databaseFactory.when(() -> DatabaseFactory.createDatabase(null)).thenThrow(new SQLException("For tests"));
			
			assertThrows(IllegalStateException.class, () -> MinerFactory.create(accountConfiguration, eventManager));
		}
	}
	
	@Test
	void nominalWithAnalyticsButNoDatabase(){
		try(var apiFactory = mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.createLoginProvider(USERNAME, loginMethod, eventManager)).thenReturn(passportApi);
			
			when(analyticsConfiguration.isEnabled()).thenReturn(true);
			when(analyticsConfiguration.getDatabase()).thenReturn(null);
			
			assertThrows(IllegalStateException.class, () -> MinerFactory.create(accountConfiguration, eventManager));
		}
	}
}