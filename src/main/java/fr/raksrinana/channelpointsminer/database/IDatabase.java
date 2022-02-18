package fr.raksrinana.channelpointsminer.database;

import fr.raksrinana.channelpointsminer.database.entity.ChannelEntity;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

public interface IDatabase extends AutoCloseable{
	void initDatabase() throws SQLException;
	
	void createChannelOrUpdate(@NotNull ChannelEntity entity) throws SQLException;
	
	void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant) throws SQLException;
	
	@Override
	void close();
	
	@NotNull
	Connection getConnection() throws SQLException;
}
