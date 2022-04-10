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
						    `LastStatusChange` DATETIME NOT NULL
						)
						ENGINE=InnoDB DEFAULT CHARSET=utf8;""",
				"""
						CREATE TABLE IF NOT EXISTS `Balance` (
							`ID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
							`ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel`(`ID`),
						    `BalanceDate` DATETIME NOT NULL,
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
