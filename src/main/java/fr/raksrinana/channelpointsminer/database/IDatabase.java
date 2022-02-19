package fr.raksrinana.channelpointsminer.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

public interface IDatabase extends AutoCloseable{
	void initDatabase() throws SQLException;
	
	void createChannel(@NotNull String channelId, @NotNull String username) throws SQLException;
	
	void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant) throws SQLException;
	
	void addBalance(@NotNull String channelId, int balance, @Nullable String reason, @NotNull Instant balanceInstant) throws SQLException;
	
	@Override
	void close();
	
	@NotNull
	Connection getConnection() throws SQLException;
}
