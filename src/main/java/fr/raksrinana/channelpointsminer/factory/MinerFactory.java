package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.channelpointsminer.config.AccountConfiguration;
import fr.raksrinana.channelpointsminer.miner.Miner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
				new TwitchWebSocketPool(),
				Executors.newScheduledThreadPool(4),
				Executors.newCachedThreadPool());
		
		miner.addHandler(MessageHandlerFactory.createLogger(miner));
		miner.addHandler(MessageHandlerFactory.createClaimAvailableHandler(miner));
		miner.addHandler(MessageHandlerFactory.createStreamStartEndHandler(miner));
		miner.addHandler(MessageHandlerFactory.createFollowRaidHandler(miner));
		miner.addHandler(MessageHandlerFactory.createPredictionsHandler(miner, BetPlacerFactory.created(miner)));
		
		if(Objects.nonNull(config.getDiscordWebhook())){
			var discordApi = ApiFactory.createdDiscordApi(config.getDiscordWebhook());
			miner.addHandler(MessageHandlerFactory.createDiscordLogger(miner, discordApi));
		}
		
		return miner;
	}
}
