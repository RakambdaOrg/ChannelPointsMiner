package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import static java.time.ZoneOffset.UTC;

@RequiredArgsConstructor
public abstract class BaseDatabase implements IDatabase{
	private final HikariDataSource dataSource;
	
	@Override
	public void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						UPDATE `Channel` SET
						`LastStatusChange` = ?
						WHERE `ID` = ?;"""
				)){
			
			var timestamp = LocalDateTime.now(UTC);
			
			statement.setObject(1, timestamp);
			statement.setString(2, channelId);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void addBalance(@NotNull String channelId, int balance, @Nullable String reason, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO `Balance`(`ChannelId`, `BalanceDate`, `Balance`, `Reason`)
						VALUES(?, ?, ?, ?);"""
				)){
			
			statement.setString(1, channelId);
			statement.setObject(2, LocalDateTime.ofInstant(instant, UTC));
			statement.setInt(3, balance);
			statement.setString(4, reason);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void addPrediction(@NotNull String channelId, @NotNull String eventId, @NotNull String type, @NotNull String description, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO `Prediction`(`ChannelId`, `EventId`, `EventDate`, `Type`, `Description`)
						VALUES(?, ?, ?, ?, ?);"""
				)){
			
			statement.setString(1, channelId);
			statement.setString(2, eventId);
			statement.setObject(3, LocalDateTime.ofInstant(instant, UTC));
			statement.setString(4, type);
			statement.setString(5, description);
			
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
	
	protected void execute(@NotNull String... statements) throws SQLException{
		try(var conn = getConnection()){
			conn.setAutoCommit(false);
			for(var sql : statements){
				try(var statement = conn.createStatement()){
					statement.execute(sql);
				}
				catch(SQLException e){
					conn.rollback();
					throw e;
				}
			}
			conn.commit();
		}
	}
}
