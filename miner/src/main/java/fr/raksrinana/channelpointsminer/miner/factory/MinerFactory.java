package fr.raksrinana.channelpointsminer.miner.factory;

import com.zaxxer.hikari.pool.HikariPool;
import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.miner.miner.Miner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinerFactory{
	@NotNull
	public static Miner create(@NotNull AccountConfiguration config){
		try{
			var dbConfig = config.getAnalytics().getDatabase();
			var database = DatabaseFactory.createDatabase(dbConfig);
			
			var miner = new Miner(
					config,
					ApiFactory.createPassportApi(config.getUsername(), config.getPassword(), config.getAuthenticationFolder(), config.isUse2Fa(), config.getBrowser()),
					new StreamerSettingsFactory(config),
					new TwitchPubSubWebSocketPool(50),
					Executors.newScheduledThreadPool(4),
					Executors.newCachedThreadPool(),
					database);
			
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createClaimAvailableHandler(miner));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createStreamStartEndHandler(miner));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createFollowRaidHandler(miner));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createPredictionsHandler(miner, BetPlacerFactory.created(miner)));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createPointsHandler(miner));
			miner.addPubSubHandler(PubSubMessageHandlerFactory.createClaimMomentHandler(miner));
			
			miner.addEventHandler(LogEventListenerFactory.createLogger());
			if(Objects.nonNull(config.getDiscord().getUrl())){
				var discordApi = ApiFactory.createdDiscordApi(config.getDiscord().getUrl());
				miner.addEventHandler(LogEventListenerFactory.createDiscordLogger(discordApi, config.getDiscord().isEmbeds()));
			}
			
			if(config.getAnalytics().isEnabled()){
				if(Objects.isNull(dbConfig)){
					throw new IllegalStateException("Analytics is enabled but no database is defined");
				}
				
				database.deleteAllUserPredictions();
				miner.addEventHandler(DatabaseFactory.createDatabaseHandler(database));
			}
			
			return miner;
		}
		catch(SQLException | HikariPool.PoolInitializationException e){
			throw new IllegalStateException("Failed to set up database", e);
		}
	}
}
