package fr.rakambda.channelpointsminer.miner.database;

import fr.rakambda.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface IDatabase extends AutoCloseable{
	void initDatabase() throws SQLException;
	
	@Override
	void close() throws IOException;
	
	void createChannel(@NonNull String channelId, @NonNull String username) throws SQLException;
	
	void updateChannelStatusTime(@NonNull String channelId, @NonNull Instant instant) throws SQLException;
	
	@NonNull
	Optional<String> getStreamerIdFromName(@NonNull String channelName) throws SQLException;
	
	void addBalance(@NonNull String channelId, int balance, @Nullable String reason, @NonNull Instant instant) throws SQLException;
	
	void addPrediction(@NonNull String channelId, @NonNull String eventId, @NonNull String type, @NonNull String description, @NonNull Instant instant) throws SQLException;
	
	int addUserPrediction(@NonNull String username, @NonNull String streamerId, @NonNull String badge) throws SQLException;
	
	void cancelPrediction(@NonNull Event event) throws SQLException;
	
	void resolvePrediction(@NonNull Event event, @NonNull String outcome, @NonNull String badge, double returnOnInvestment) throws SQLException;
	
	void deleteAllUserPredictions() throws SQLException;
	
	void deleteUserPredictionsForChannel(@NonNull String channelId) throws SQLException;
	
	@NonNull
	Collection<OutcomeStatistic> getOutcomeStatisticsForChannel(@NonNull String channelId, int minBetsPlacedByUser) throws SQLException;
}
