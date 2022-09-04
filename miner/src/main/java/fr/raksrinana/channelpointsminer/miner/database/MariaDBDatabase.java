package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MariaDBDatabase extends BaseDatabase{
	public MariaDBDatabase(HikariDataSource dataSource){
		super(dataSource);
	}
	
	@Override
	public void initDatabase(){
		applyFlyway("db/migrations/mariadb");
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
	
	@Override
	protected void addUserPrediction(@NotNull String channelId, int userId, @NotNull String badge) throws SQLException{
		try(var conn = getConnection();
				var predictionStatement = conn.prepareStatement("""
						INSERT IGNORE INTO `UserPrediction`(`ChannelID`, `UserID`, `Badge`) VALUES (?,?,?)"""
				)){
			
			predictionStatement.setString(1, channelId);
			predictionStatement.setInt(2, userId);
			predictionStatement.setString(3, badge);
			
			predictionStatement.executeUpdate();
		}
	}
	
	@NotNull
	@Override
	protected PreparedStatement getUpdatePredictionUserStmt(@NotNull Connection conn) throws SQLException{
		return conn.prepareStatement("""
				UPDATE `PredictionUser`
				SET
				`PredictionCnt`=`PredictionCnt`+1,
				`WinCnt`=`WinCnt`+?,
				`WinRate`=`WinCnt`/`PredictionCnt`,
				`ReturnOnInvestment`=`ReturnOnInvestment`+?
				WHERE `ID`=? AND `ChannelID`=?""");
	}
}