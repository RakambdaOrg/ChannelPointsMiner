package fr.raksrinana.channelpointsminer.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.raksrinana.channelpointsminer.database.entity.ChannelEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@RequiredArgsConstructor
public class MariaDBDatabase implements IDatabase{
	private final HikariDataSource dataSource;
	
	@Override
	public void initDatabase() throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						CREATE TABLE IF NOT EXISTS `Channel` (
							`ID` VARCHAR(32) NOT NULL PRIMARY KEY,
						    `Username` VARCHAR(128) NOT NULL,
						    `LastStatusChange` DATETIME NOT NULL
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;"""
				)){
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void createChannelOrUpdate(@NotNull ChannelEntity entity) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO `Channel`(ID, Username, LastStatusChange)
						VALUES(?, ?, ?)
						ON DUPLICATE KEY UPDATE LastStatusChange = ?;"""
				)){
			
			var timestamp = Timestamp.from(entity.getLastStatusChange());
			
			statement.setString(1, entity.getId());
			statement.setString(2, entity.getUsername());
			statement.setTimestamp(3, timestamp);
			statement.setTimestamp(4, timestamp);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						UPDATE `Channel` SET
						LastStatusChange = ?
						WHERE ID = ?;"""
				)){
			
			var timestamp = Timestamp.from(instant);
			
			statement.setTimestamp(1, timestamp);
			statement.setString(2, channelId);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void close(){
		dataSource.close();
	}
	
	@NotNull
	@Override
	public Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}
}
