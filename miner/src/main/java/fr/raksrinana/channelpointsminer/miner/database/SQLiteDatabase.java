package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLiteDatabase extends BaseDatabase{
	public SQLiteDatabase(HikariDataSource dataSource){
		super(dataSource);
	}
	
	@Override
	public void initDatabase() throws SQLException{
		applyFlyway("db/migrations/sqlite");
	}
	
	@Override
	public void createChannel(@NotNull String channelId, @NotNull String username) throws SQLException{
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
	protected void addUserPrediction(@NotNull String channelId, int userId, @NotNull String badge) throws SQLException{
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
	
	@NotNull
	@Override
	protected PreparedStatement getUpdatePredictionUserStmt(@NotNull Connection conn) throws SQLException{
		return conn.prepareStatement("""
				WITH wi AS (SELECT ? AS n)
				UPDATE `PredictionUser`
				SET
				`PredictionCnt`=`PredictionCnt`+1,
				`WinCnt`=`WinCnt`+wi.n,
				`WinRate`=CAST((`WinCnt`+wi.n) AS REAL)/(`PredictionCnt`+1),
				`ReturnOnInvestment`=`ReturnOnInvestment`+?
				FROM wi
				WHERE `ID`=? AND `ChannelID`=?""");
	}
}
