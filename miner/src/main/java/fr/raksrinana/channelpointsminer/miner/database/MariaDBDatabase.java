package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;

public class MariaDBDatabase extends BaseDatabase{
	public MariaDBDatabase(HikariDataSource dataSource){
		super(dataSource);
	}
	
	@Override
	public void initDatabase() throws SQLException{
		execute("""
						CREATE TABLE IF NOT EXISTS `Channel` (
							`ID` VARCHAR(32) NOT NULL PRIMARY KEY,
							`Username` VARCHAR(128) NOT NULL,
							`LastStatusChange` DATETIME NOT NULL,
							INDEX `UsernameIdx`(`Username`)
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""",
				"""
						CREATE TABLE IF NOT EXISTS `Balance` (
							`ID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
							`BalanceDate` DATETIME(3) NOT NULL,
							`Balance` INT NOT NULL,
							`Reason` VARCHAR(16) NULL,
							INDEX `PointsDateIdx`(`BalanceDate`)
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""",
				"""
						CREATE TABLE IF NOT EXISTS `Prediction` (
							`ID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
							`EventID` VARCHAR(36) NOT NULL,
							`EventDate` DATETIME NOT NULL,
							`Type` VARCHAR(16) NULL,
							`Description` VARCHAR(255) NULL,
							INDEX `EventDateIdx`(`EventDate`),
							INDEX `EventTypeIdx`(`Type`)
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""",
				"""
						CREATE TABLE IF NOT EXISTS `ResolvedPrediction` (
							`EventID` VARCHAR(36) NOT NULL PRIMARY KEY,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
							`Title` VARCHAR(64) NOT NULL,
							`EventCreated` DATETIME NOT NULL,
							`EventEnded` DATETIME NULL,
							`Canceled` BOOLEAN NOT NULL,
							`Outcome` VARCHAR(32) NULL,
							`Badge` VARCHAR(32) NULL,
							INDEX `ChannelIDIdx`(`ChannelID`)
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""",
				"""
						CREATE TABLE IF NOT EXISTS `PredictionUser` (
							`ID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
							`Username` VARCHAR(128) NOT NULL,
							`PredictionCnt` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
							`WinCnt` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
							UNIQUE (`Username`),
							INDEX `UsernameIdx`(`Username`)
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""",
				"""
						CREATE TABLE IF NOT EXISTS `UserPrediction` (
							 `ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
							 `UserID` INT NOT NULL REFERENCES `PredictionUser`(`ID`),
							 `ResolvedPredictionID` VARCHAR(36) NOT NULL DEFAULT '',
							 `Badge` VARCHAR(32) NOT NULL,
							 PRIMARY KEY (`ChannelID`, `UserID`, `ResolvedPredictionID`),
							 INDEX `ChannelIDIdx`(`ChannelID`),
							 INDEX `UserIDIdx`(`UserID`),
							 INDEX `ResolvedPredictionIDIdx`(`ResolvedPredictionID`)
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
			
			statement.setString(1, channelId);
			statement.setString(2, username);
			
			statement.executeUpdate();
		}
	}
}