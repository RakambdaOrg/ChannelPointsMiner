package fr.raksrinana.channelpointsminer.miner.database;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
	public int addUserPrediction(@NotNull String username, @NotNull String streamerName, @NotNull String badge){
		return -1;
	}
	
	@Override
	public void cancelPrediction(@NotNull Event event){
	}
	
	@Override
	public void resolvePrediction(@NotNull Event event, @NotNull String outcome, @NotNull String badge, double returnOnInvestment){
	}
	
	@Override
	public void deleteUserPredictions(){
	}
	
	@Override
	public void deleteUserPredictionsForChannel(@NotNull String channelId){
	}
	
	@Override
	@NotNull
	public Collection<OutcomeStatistic> getOutcomeStatisticsForChannel(@NotNull String channelId, int minBetsPlacedByUser){
		return List.of();
	}
	
	@Override
	public void close(){
	}
	
	@Override
	@NotNull
	public Optional<String> getStreamerIdFromName(@NotNull String channelName) throws SQLException{
		return Optional.empty();
	}
}
