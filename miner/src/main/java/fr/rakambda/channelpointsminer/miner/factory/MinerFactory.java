package fr.rakambda.channelpointsminer.miner.factory;

import com.zaxxer.hikari.pool.HikariPool;
import fr.rakambda.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketPool;
import fr.rakambda.channelpointsminer.miner.config.AccountConfiguration;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.miner.Miner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinerFactory{
	@NotNull
	public static Miner create(@NotNull AccountConfiguration config, @NotNull IEventManager eventManager){
		try{
			var dbConfig = config.getAnalytics().getDatabase();
			var database = DatabaseFactory.createDatabase(dbConfig);
			
			var miner = new Miner(
					config,
					ApiFactory.createLoginProvider(config.getUsername(), config.getLoginMethod(), eventManager),
					new StreamerSettingsFactory(config),
					new TwitchPubSubWebSocketPool(50),
					Executors.newScheduledThreadPool(4),
					Executors.newCachedThreadPool(),
					database,
					eventManager);
			
			var syncInventory = MinerRunnableFactory.createSyncInventory(miner, eventManager);
			miner.setSyncInventory(syncInventory);
			
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createClaimAvailableHandler(miner, eventManager));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createStreamStartEndHandler(miner, eventManager));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createFollowRaidHandler(miner));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createPredictionsHandler(miner, BetPlacerFactory.created(miner), eventManager));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createPointsHandler(miner, eventManager));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createClaimMomentHandler(miner, eventManager));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createClaimDropHandler(miner, eventManager));
			
			eventManager.addEventHandler(LogEventListenerFactory.createLogger());
			if(Objects.nonNull(config.getDiscord().getUrl())){
				var discordApi = ApiFactory.createDiscordApi(config.getDiscord().getUrl());
				eventManager.addEventHandler(LogEventListenerFactory.createDiscordLogger(discordApi, config.getDiscord()));
			}
			if(Objects.nonNull(config.getTelegram().getToken())){
				var telegramApi = ApiFactory.createTelegramApi(URI.create("https://api.telegram.org/bot%s".formatted(config.getTelegram().getToken())).toURL());
				eventManager.addEventHandler(LogEventListenerFactory.createTelegramLogger(telegramApi, config.getTelegram()));
			}
			
			if(config.getAnalytics().isEnabled()){
				if(Objects.isNull(dbConfig)){
					throw new IllegalStateException("Analytics is enabled but no database is defined");
				}
				
				database.deleteAllUserPredictions();
				eventManager.addEventHandler(DatabaseFactory.createDatabaseHandler(database, config.getAnalytics().isRecordUserPredictions()));
			}
			
			return miner;
		}
		catch(SQLException | HikariPool.PoolInitializationException e){
			throw new IllegalStateException("Failed to set up database", e);
		}
		catch(MalformedURLException e){
			throw new IllegalStateException("Failed to create an url", e);
		}
	}
}
