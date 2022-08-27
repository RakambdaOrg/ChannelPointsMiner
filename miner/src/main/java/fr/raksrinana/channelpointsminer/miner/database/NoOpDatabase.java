package fr.raksrinana.channelpointsminer.miner.database;

import fr.raksrinana.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class NoOpDatabase implements IDatabase{
	@Override
	public void initDatabase(){
	}
	
	@Override
	public void createChannel(@NotNull String channelId, @NotNull String username){
	}
	
	@Override
	public void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant){
	}
	
	@Override
	public void addBalance(@NotNull String channelId, int balance, @Nullable String reason, @NotNull Instant instant){
	}
	
	@Override
	public void addPrediction(@NotNull String channelId, @NotNull String eventId, @NotNull String type, @NotNull String description, @NotNull Instant instant){
	}
	
	@Override
	public void addUserPrediction(@NotNull String username, @NotNull String streamerName, @NotNull String badge){
	}
	
	@Override
	public void cancelPrediction(@NotNull String eventId, @NotNull String channelId, @NotNull String title, @NotNull Instant eventCreated, @NotNull Instant eventEnded){
	}
	
	@Override
	public void resolvePrediction(@NotNull String eventId, @NotNull String channelId, @NotNull String title, @NotNull Instant eventCreated, @NotNull Instant eventEnded, @NotNull String outcome, @NotNull String badge, double returnOnInvestment){
	}
	
	@Override
	public void deleteUnresolvedUserPredictions(){
	}
	
	@Override
	public void deleteUnresolvedUserPredictionsForChannel(@NotNull String channelId){
	}
	
	@Override
	@NotNull
	public Collection<OutcomeStatistic> getOutcomeStatisticsForChannel(@NotNull String channelId, int minBetsPlacedByUser){
		return List.of();
	}
	
	@Override
	public void close(){
	}
}
