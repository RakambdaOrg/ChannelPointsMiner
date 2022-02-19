package fr.raksrinana.channelpointsminer.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static java.time.ZoneOffset.UTC;

public class SQLiteDatabase extends BaseDatabase{
	public SQLiteDatabase(HikariDataSource dataSource){
		super(dataSource);
	}
	
	@Override
	public void initDatabase() throws SQLException{
		execute("""
						CREATE TABLE IF NOT EXISTS `Channel` (
							`ID` VARCHAR(32) NOT NULL PRIMARY KEY,
						    `Username` VARCHAR(128) NOT NULL,
						    `LastStatusChange` DATETIME NOT NULL
						);""",
				"""
						CREATE TABLE IF NOT EXISTS `Balance` (
							`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
						    `BalanceDate` DATETIME NOT NULL,
						    `Balance` INTEGER NOT NULL,
						    `Reason` VARCHAR(16) NULL
						);""",
				"""
						CREATE INDEX IF NOT EXISTS `PointsDateIdx` ON `Balance`(`BalanceDate`);""",
				"""
						CREATE TABLE IF NOT EXISTS `Prediction` (
							`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
							`EventID` VARCHAR(36) NOT NULL,
							`EventDate` DATETIME NOT NULL,
							`Type` VARCHAR(16) NULL,
							`Description` VARCHAR(255) NULL
						);""",
				"""
						CREATE INDEX IF NOT EXISTS `EventDateIdx` ON `Prediction`(`EventDate`);""",
				"""
						CREATE INDEX IF NOT EXISTS `EventTypeIdx` ON `Prediction`(`Type`);""");
	}
	
	@Override
	public void createChannel(@NotNull String channelId, @NotNull String username) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT OR IGNORE INTO `Channel`(`ID`, `Username`, `LastStatusChange`)
						VALUES(?, ?, ?);"""
				)){
			
			var timestamp = LocalDateTime.now(UTC);
			
			statement.setString(1, channelId);
			statement.setString(2, username);
			statement.setObject(3, timestamp);
			
			statement.executeUpdate();
		}
	}
}
