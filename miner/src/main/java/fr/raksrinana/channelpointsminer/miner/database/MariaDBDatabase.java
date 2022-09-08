package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.raksrinana.channelpointsminer.miner.database.converter.Converters;
import org.jetbrains.annotations.NotNull;
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
	
	@Override
	protected void resolveUserPredictions(double returnRatioForWin, @NotNull String channelId, @NotNull String badge) throws SQLException{
		try(var conn = getConnection();
				var getOpenPredictionStmt = conn.prepareStatement("""
						SELECT * FROM `UserPrediction` WHERE `ChannelID`=?""");
				var updatePredictionUserStmt = conn.prepareStatement("""
						UPDATE `PredictionUser`
						SET
						`PredictionCnt`=`PredictionCnt`+1,
						`WinCnt`=`WinCnt`+?,
						`WinRate`=`WinCnt`/`PredictionCnt`,
						`ReturnOnInvestment`=`ReturnOnInvestment`+?
						WHERE `ID`=? AND `ChannelID`=?""")
		){
			double returnOnInvestment = returnRatioForWin - 1;
			
			getOpenPredictionStmt.setString(1, channelId);
			try(var result = getOpenPredictionStmt.executeQuery()){
				while(result.next()){
					var userPrediction = Converters.convertUserPrediction(result);
					if(badge.equals(userPrediction.getBadge())){
						updatePredictionUserStmt.setInt(1, 1);
						updatePredictionUserStmt.setDouble(2, returnOnInvestment);
					}
					else{
						updatePredictionUserStmt.setInt(1, 0);
						updatePredictionUserStmt.setDouble(2, -1);
					}
					updatePredictionUserStmt.setInt(3, userPrediction.getUserId());
					updatePredictionUserStmt.setString(4, userPrediction.getChannelId());
					updatePredictionUserStmt.addBatch();
				}
				updatePredictionUserStmt.executeBatch();
			}
		}
	}
}