package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
							`Badge` VARCHAR(32) NULL,
							`ReturnRatioForWin` REAL NULL
						);""",
				"""
						CREATE INDEX IF NOT EXISTS `ChannelIDIdx` ON `ResolvedPrediction`(`ChannelID`);""",
				"""
						CREATE TABLE IF NOT EXISTS `PredictionUser` (
							`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
							`Username` VARCHAR(128) NOT NULL,
							`PredictionCnt` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
							`WinCnt` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
							`WinRate` REAL NOT NULL DEFAULT 0,
							`ReturnOnInvestment` REAL NOT NULL DEFAULT 0,
							UNIQUE (`Username`)
						);""",
				"""
						CREATE INDEX IF NOT EXISTS `UsernameIdx` ON `PredictionUser`(`Username`);""",
				"""
						CREATE TABLE IF NOT EXISTS `UserPrediction` (
						     `ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
						     `UserID` INTEGER NOT NULL REFERENCES `PredictionUser`(`ID`),
						     `Badge` VARCHAR(32) NOT NULL,
						     PRIMARY KEY (`ChannelID`, `UserID`)
						);""",
				"""
						CREATE INDEX IF NOT EXISTS `ChannelIDIdx` ON `UserPrediction`(`ChannelID`);""",
				"""
						CREATE INDEX IF NOT EXISTS `UserIDIdx` ON `UserPrediction`(`UserID`);""");
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
	
	@NotNull
	@Override
	protected PreparedStatement getPredictionStmt(@NotNull Connection conn) throws SQLException{
		return conn.prepareStatement("""
				INSERT OR IGNORE INTO `UserPrediction`(`ChannelID`, `UserID`, `Badge`)
				SELECT c.`ID`, ?, ? FROM `Channel` AS c WHERE c.`Username`=?"""
		);
	}
	
	@NotNull
	@Override
	protected PreparedStatement getUpdatePredictionUserStmt(@NotNull Connection conn) throws SQLException{
		return conn.prepareStatement("""
				WITH wi AS (SELECT ? AS n)
				UPDATE `PredictionUser`
				SET `PredictionCnt`=`PredictionCnt`+1, `WinCnt`=`WinCnt`+wi.n,
				`WinRate`=CAST((`WinCnt`+wi.n) AS REAL)/(`PredictionCnt`+1), `ReturnOnInvestment`=`ReturnOnInvestment`+? FROM wi WHERE `ID`=?""");
	}
}
