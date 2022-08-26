package fr.raksrinana.channelpointsminer.miner.factory;

import com.zaxxer.hikari.pool.HikariPool;
import fr.raksrinana.channelpointsminer.miner.api.chat.TwitchChatFactory;
import fr.raksrinana.channelpointsminer.miner.api.ws.TwitchPubSubWebSocketPool;
import fr.raksrinana.channelpointsminer.miner.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.event.IEventListener;
import fr.raksrinana.channelpointsminer.miner.miner.Miner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinerFactory{
	@NotNull
	public static Miner create(@NotNull AccountConfiguration config){
        
        boolean recordPredictions = false;
        IDatabase database;
        List<IEventListener> minerEventListeners = new LinkedList<>();
        
        TwitchChatFactory twitchChatFactory;
        
        if(config.getAnalytics().isEnabled()){
            try{
                var dbConfig = config.getAnalytics().getDatabase();
                if(Objects.isNull(dbConfig)){
                    throw new IllegalStateException("Analytics is enabled but no database is defined");
                }
                database = DatabaseFactory.createDatabase(dbConfig);
                if(config.getAnalytics().isRecordChatsPredictions()){
                    recordPredictions = true;
                    database.deleteUnresolvedUserPredictions();
                }
                minerEventListeners.add(DatabaseFactory.createDatabaseHandler(database));
    
                twitchChatFactory = new TwitchChatFactory(database);
            }
            catch(SQLException | HikariPool.PoolInitializationException e){
                throw new IllegalStateException("Failed to set up database", e);
            }
        }
        else {
            twitchChatFactory = new TwitchChatFactory();
        }
        
        minerEventListeners.add(LogEventListenerFactory.createLogger());
        if(Objects.nonNull(config.getDiscord().getUrl())){
            var discordApi = ApiFactory.createdDiscordApi(config.getDiscord().getUrl());
            minerEventListeners.add(LogEventListenerFactory.createDiscordLogger(discordApi, config.getDiscord().isEmbeds()));
        }
        
		var miner = new Miner(
				config,
				ApiFactory.createPassportApi(config.getUsername(), config.getPassword(), config.getAuthenticationFolder(), config.isUse2Fa()),
				new StreamerSettingsFactory(config),
				new TwitchPubSubWebSocketPool(50),
				Executors.newScheduledThreadPool(4),
				Executors.newCachedThreadPool(),
                twitchChatFactory,
                minerEventListeners);
		
		miner.addHandler(MessageHandlerFactory.createClaimAvailableHandler(miner));
		miner.addHandler(MessageHandlerFactory.createStreamStartEndHandler(miner));
		miner.addHandler(MessageHandlerFactory.createFollowRaidHandler(miner));
		miner.addHandler(MessageHandlerFactory.createPredictionsHandler(miner, BetPlacerFactory.created(miner), recordPredictions));
		miner.addHandler(MessageHandlerFactory.createPointsHandler(miner));
  
		return miner;
	}
}
