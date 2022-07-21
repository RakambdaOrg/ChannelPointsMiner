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
		var miner = new Miner(
				config,
				ApiFactory.createPassportApi(config.getUsername(), config.getPassword(), config.getAuthenticationFolder(), config.isUse2Fa()),
				new StreamerSettingsFactory(config),
				new TwitchPubSubWebSocketPool(50),
				Executors.newScheduledThreadPool(4),
				Executors.newCachedThreadPool());
		
		miner.addHandler(MessageHandlerFactory.createClaimAvailableHandler(miner));
		miner.addHandler(MessageHandlerFactory.createStreamStartEndHandler(miner));
		miner.addHandler(MessageHandlerFactory.createFollowRaidHandler(miner));
		miner.addHandler(MessageHandlerFactory.createPredictionsHandler(miner, BetPlacerFactory.created(miner)));
		miner.addHandler(MessageHandlerFactory.createPointsHandler(miner));
		
		miner.addEventListener(LogEventListenerFactory.createLogger());
		if(Objects.nonNull(config.getDiscord().getUrl())){
			var discordApi = ApiFactory.createdDiscordApi(config.getDiscord().getUrl());
			miner.addEventListener(LogEventListenerFactory.createDiscordLogger(discordApi, config.getDiscord().isEmbeds()));
		}
		
		if(config.getAnalytics().isEnabled()){
			var dbConfig = config.getAnalytics().getDatabase();
			if(Objects.isNull(dbConfig)){
				throw new IllegalStateException("Analytics is enabled but no database is defined");
			}
			try{
				var database = DatabaseFactory.createDatabase(dbConfig);
				miner.addEventListener(DatabaseFactory.createDatabaseHandler(database));
			}
			catch(SQLException | HikariPool.PoolInitializationException e){
				throw new IllegalStateException("Failed to set up database", e);
			}
		}
		
		return miner;
	}
}
