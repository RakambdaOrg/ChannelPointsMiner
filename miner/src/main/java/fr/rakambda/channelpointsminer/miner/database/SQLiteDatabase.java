package fr.rakambda.channelpointsminer.miner.database;

import fr.rakambda.channelpointsminer.miner.database.converter.Converters;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import org.jspecify.annotations.NonNull;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLiteDatabase extends BaseDatabase{
	public SQLiteDatabase(DataSource dataSource){
		super(dataSource);
	}
	
	@Override
	public void initDatabase(){
		applyFlyway("db/migrations/sqlite");
	}
	
	@Override
	public void createChannel(@NonNull String channelId, @NonNull String username) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT OR IGNORE INTO `Channel`(`ID`, `Username`, `LastStatusChange`)
						VALUES(?, ?, ?);"""
				)){
			
			statement.setString(1, channelId);
			statement.setString(2, username);
			statement.setTimestamp(3, Timestamp.from(TimeFactory.now()));
			
			statement.executeUpdate();
		}
	}
	
	@Override
	protected void addUserPrediction(@NonNull String channelId, int userId, @NonNull String badge) throws SQLException{
		try(var conn = getConnection();
				var predictionStatement = conn.prepareStatement("""
						INSERT OR IGNORE INTO `UserPrediction`(`ChannelID`, `UserID`, `Badge`)
						VALUES(?,?,?)"""
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
						WITH wi AS (SELECT ? AS n)
						UPDATE `PredictionUser`
						SET
						`PredictionCnt`=`PredictionCnt`+1,
						`WinCnt`=`WinCnt`+wi.n,
						`WinRate`=CAST((`WinCnt`+wi.n) AS REAL)/(`PredictionCnt`+1),
						`ReturnOnInvestment`=`ReturnOnInvestment`+?
						FROM wi
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
