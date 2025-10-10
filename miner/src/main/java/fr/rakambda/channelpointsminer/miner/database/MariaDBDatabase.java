package fr.rakambda.channelpointsminer.miner.database;

import fr.rakambda.channelpointsminer.miner.database.converter.Converters;
import org.jspecify.annotations.NonNull;
import javax.sql.DataSource;
import java.sql.SQLException;

public class MariaDBDatabase extends BaseDatabase{
	public MariaDBDatabase(DataSource dataSource){
		super(dataSource);
	}
	
	@Override
	public void initDatabase(){
		applyFlyway("db/migrations/mariadb");
	}
	
	@Override
	public void createChannel(@NonNull String channelId, @NonNull String username) throws SQLException{
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
	protected void addUserPrediction(@NonNull String channelId, int userId, @NonNull String badge) throws SQLException{
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
	protected void resolveUserPredictions(double returnRatioForWin, @NonNull String channelId, @NonNull String badge) throws SQLException{
		try(var conn = getConnection();
				var getOpenPredictionStmt = conn.prepareStatement("""
						SELECT * FROM `UserPrediction` WHERE `ChannelID`=?""");
				var updatePredictionUserStmt = conn.prepareStatement("""
						UPDATE `PredictionUser`
						SET
						`PredictionCnt`=`PredictionCnt`+1,
						`WinCnt`=`WinCnt`+?,
						`WinRate`=(`WinCnt` + ?)/`PredictionCnt`,
						`ReturnOnInvestment`=`ReturnOnInvestment`+?
						WHERE `ID`=? AND `ChannelID`=?""")
		){
			double returnOnInvestment = returnRatioForWin - 1;
			
			getOpenPredictionStmt.setString(1, channelId);
			try(var result = getOpenPredictionStmt.executeQuery()){
				while(result.next()){
					var userPrediction = Converters.convertUserPrediction(result);
					boolean isWinner = badge.equals(userPrediction.getBadge());
					
					updatePredictionUserStmt.setInt(1, isWinner ? 1 : 0);
					updatePredictionUserStmt.setInt(2, isWinner ? 1 : 0);
					updatePredictionUserStmt.setDouble(3, isWinner ? returnOnInvestment : -1);
					updatePredictionUserStmt.setInt(4, userPrediction.getUserId());
					updatePredictionUserStmt.setString(5, userPrediction.getChannelId());
					updatePredictionUserStmt.addBatch();
				}
				updatePredictionUserStmt.executeBatch();
			}
		}
	}
}
