package fr.raksrinana.channelpointsminer.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import static java.time.ZoneOffset.UTC;

@RequiredArgsConstructor
public class MariaDBDatabase implements IDatabase{
	private final HikariDataSource dataSource;
	
	@Override
	public void initDatabase() throws SQLException{
		execute("""
						CREATE TABLE IF NOT EXISTS `Channel` (
							`ID` VARCHAR(32) NOT NULL PRIMARY KEY,
						    `Username` VARCHAR(128) NOT NULL,
						    `LastStatusChange` DATETIME NOT NULL
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""",
				"""
						CREATE TABLE IF NOT EXISTS `Balance` (
							`ID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
						    `BalanceDate` DATETIME NOT NULL,
						    `Balance` INT NOT NULL,
						    INDEX `PointsDateIdx`(`BalanceDate`)
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""");
	}
	
	@Override
	public void createChannel(@NotNull String channelId, @NotNull String username) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT IGNORE INTO `Channel`(`ID`, `Username`, `LastStatusChange`)
						VALUES(?, ?, NOW());"""
				)){
			
			var timestamp = LocalDateTime.now(UTC);
			
			statement.setString(1, channelId);
			statement.setString(2, username);
			statement.setObject(3, timestamp);
			statement.setObject(4, timestamp);
			
			statement.executeUpdate();
		}
	}
	
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
	public void addBalance(@NotNull String channelId, int balance, @NotNull Instant balanceInstant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO `Balance`(`ChannelId`, `BalanceDate`, `Balance`)
						VALUES(?, ?, ?);"""
				)){
			
			statement.setString(1, channelId);
			statement.setObject(2, LocalDateTime.ofInstant(balanceInstant, UTC));
			statement.setInt(3, balance);
			
			statement.executeUpdate();
		}
	}
	
	private void execute(@NotNull String... statements) throws SQLException{
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
