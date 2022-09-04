package fr.raksrinana.channelpointsminer.miner.database;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface IDatabase extends AutoCloseable{
	void initDatabase() throws SQLException;
	
	@Override
	void close();
	
	void createChannel(@NotNull String channelId, @NotNull String username) throws SQLException;
	
	void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant) throws SQLException;
	
	void addBalance(@NotNull String channelId, int balance, @Nullable String reason, @NotNull Instant instant) throws SQLException;
	
	void addPrediction(@NotNull String channelId, @NotNull String eventId, @NotNull String type, @NotNull String description, @NotNull Instant instant) throws SQLException;
	
	int addUserPrediction(@NotNull String username, @NotNull String streamerId, @NotNull String badge) throws SQLException;
	
	void cancelPrediction(@NotNull Event event) throws SQLException;
	
	void resolvePrediction(@NotNull Event event, @NotNull String outcome, @NotNull String badge, double returnOnInvestment) throws SQLException;
	
	void deleteUserPredictions() throws SQLException;
	
	void deleteUserPredictionsForChannel(@NotNull String channelId) throws SQLException;
	
	@NotNull
	Collection<OutcomeStatistic> getOutcomeStatisticsForChannel(@NotNull String channelId, int minBetsPlacedByUser) throws SQLException;
	
	@NotNull
	Optional<String> getStreamerIdFromName(@NotNull String channelName) throws SQLException;
}
