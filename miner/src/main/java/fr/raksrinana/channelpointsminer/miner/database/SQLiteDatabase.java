package fr.raksrinana.channelpointsminer.miner.database;

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
						    `BalanceDate` DATETIME(3) NOT NULL,
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
						CREATE INDEX IF NOT EXISTS `EventTypeIdx` ON `Prediction`(`Type`);""",
                """
						CREATE TABLE IF NOT EXISTS `ResolvedPrediction` (
							`EventID` VARCHAR(36) NOT NULL PRIMARY KEY,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
							`Title` VARCHAR(64) NOT NULL,
							`EventCreated` DATETIME NOT NULL,
							`EventEnded` DATETIME NULL,
							`Canceled` BOOLEAN NOT NULL,
							`Outcome` VARCHAR(32) NULL,
							`Badge` VARCHAR(32) NULL
						);""",
                """
                        CREATE INDEX IF NOT EXISTS `ChannelIDIdx` ON `ResolvedPrediction`(`ChannelID`);""",
                """
						CREATE TABLE IF NOT EXISTS `PredictionUser` (
							`ID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
							`Username` VARCHAR(128) NOT NULL,
							`PredictionCnt` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
							`WinCnt` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
							UNIQUE (`Username`)
						);""",
                """
                        CREATE INDEX IF NOT EXISTS `UsernameIdx` ON `PredictionUser`(`Username`);""",
                """
                        CREATE TABLE IF NOT EXISTS `UserPrediction` (
                             `ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
                             `UserID` INT NOT NULL REFERENCES `PredictionUser`(`ID`),
                             `ResolvedPredictionID` VARCHAR(36) NOT NULL DEFAULT '',
                             `Badge` VARCHAR(32) NOT NULL,
                             PRIMARY KEY (`ChannelID`, `UserID`, `ResolvedPredictionID`)
                        );""",
                """
                        CREATE INDEX IF NOT EXISTS `ChannelIDIdx` ON `UserPrediction`(`ChannelID`);""",
                """
                        CREATE INDEX IF NOT EXISTS `UserIDIdx` ON `UserPrediction`(`UserID`);""",
                """
                        CREATE INDEX IF NOT EXISTS `ResolvedPredictionIDIdx` ON `UserPrediction`(`ResolvedPredictionID`);""");
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
