package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.raksrinana.channelpointsminer.miner.database.converter.Converters;
import fr.raksrinana.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import static java.time.ZoneOffset.UTC;

@RequiredArgsConstructor
@Log4j2
public abstract class BaseDatabase implements IDatabase{
	private final HikariDataSource dataSource;
	
	@Override
	public void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						UPDATE `Channel` SET
						`LastStatusChange` = ?
						WHERE `ID` = ?;"""
				)){
			
			var timestamp = LocalDateTime.now(UTC);
			
			statement.setObject(1, timestamp);
			statement.setString(2, channelId);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void addBalance(@NotNull String channelId, int balance, @Nullable String reason, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO `Balance`(`ChannelId`, `BalanceDate`, `Balance`, `Reason`)
						VALUES(?, ?, ?, ?);"""
				)){
			
			statement.setString(1, channelId);
			statement.setObject(2, LocalDateTime.ofInstant(instant, UTC));
			statement.setInt(3, balance);
			statement.setString(4, reason);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void addPrediction(@NotNull String channelId, @NotNull String eventId, @NotNull String type, @NotNull String description, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO `Prediction`(`ChannelId`, `EventId`, `EventDate`, `Type`, `Description`)
						VALUES(?, ?, ?, ?, ?);"""
				)){
			
			statement.setString(1, channelId);
			statement.setString(2, eventId);
			statement.setObject(3, LocalDateTime.ofInstant(instant, UTC));
			statement.setString(4, type);
			statement.setString(5, description);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public void addUserPrediction(@NotNull String username, @NotNull String streamerName, @NotNull String badge) throws SQLException{
		
		try(var conn = getConnection(); var selectUserStatement = conn.prepareStatement("""
						SELECT `ID` FROM `PredictionUser` WHERE `Username`=?""");
			var predictionStatement = getPredictionStatement(conn)){
			conn.setAutoCommit(false);
			
			username = username.toLowerCase(Locale.ROOT);
			
			selectUserStatement.setString(1, username);
			var userResult = selectUserStatement.executeQuery();
			int userId;
			if(userResult.next()){
				userId = userResult.getInt(1);
			}
			else {
				try(var addUserStatement = conn.prepareStatement("""
						INSERT INTO `PredictionUser`(`Username`) VALUES (?) RETURNING `ID`""")){
					addUserStatement.setString(1, username);
					var insertResult = addUserStatement.executeQuery();
					insertResult.next();
					userId = insertResult.getInt(1);
				}
				log.debug("Added new prediction user '{}'", username);
			}
	
			predictionStatement.setInt(1, userId);
			predictionStatement.setString(2, badge);
			predictionStatement.setString(3, streamerName);
		
			predictionStatement.executeUpdate();
			conn.commit();
		}
	}
    
    protected abstract PreparedStatement getPredictionStatement(Connection conn) throws SQLException;
	
	@Override
	public void cancelPrediction(@NotNull String eventId, @NotNull String channelId, @NotNull String title, @NotNull Instant eventCreated,
			@NotNull Instant eventEnded) throws SQLException{
		try(var conn = getConnection();
				var addCanceledPredictionStmt = conn.prepareStatement("""
						INSERT INTO `ResolvedPrediction`(`EventID`,`ChannelID`, `Title`,`EventCreated`,`EventEnded`,`Canceled`)
						VALUES (?,?,?,?,?,true)""");
				var updateUserPredictionsStmt = conn.prepareStatement("""
						UPDATE `UserPrediction`
						SET `ResolvedPredictionID`=?
						WHERE `ResolvedPredictionID`='' AND `ChannelID`=?""")
		){
			conn.setAutoCommit(false);
			try{
				//Add canceled prediction
				addCanceledPredictionStmt.setString(1, eventId);
				addCanceledPredictionStmt.setString(2, channelId);
				addCanceledPredictionStmt.setString(3, title);
				addCanceledPredictionStmt.setObject(4, LocalDateTime.ofInstant(eventCreated, UTC));
				addCanceledPredictionStmt.setObject(5, LocalDateTime.ofInstant(eventEnded, UTC));
				addCanceledPredictionStmt.executeUpdate();
	
				//Update made predictions with event-id
				updateUserPredictionsStmt.setString(1, eventId);
				updateUserPredictionsStmt.setString(2, channelId);
				updateUserPredictionsStmt.executeUpdate();
	
				conn.commit();
			}
			catch(SQLException e){
				conn.rollback();
				throw e;
			}
		}
	}
	
	@Override
	public void resolvePrediction(@NotNull String eventId, @NotNull String channelId, @NotNull String title, @NotNull Instant eventCreated,
			@NotNull Instant eventEnded, @NotNull String outcome, @NotNull String badge, double returnRatioForWin) throws SQLException{
		try(var conn = getConnection();
				var getOpenPredictionStmt = conn.prepareStatement("""
						SELECT * FROM `UserPrediction` WHERE `ResolvedPredictionID`='' AND `ChannelID`=?""");
				var updatePredictionUserStmt = getUpdatePredictionUserStmt(conn);
				var addResolvedPredictionStmt = conn.prepareStatement("""
						INSERT INTO `ResolvedPrediction`(`EventID`,`ChannelID`, `Title`,`EventCreated`,`EventEnded`,`Canceled`,`Outcome`,`Badge`,`ReturnRatioForWin`)
						VALUES (?,?,?,?,?,false,?,?,?)""");
				var updateUserPredictionsStmt = conn.prepareStatement("""
						UPDATE `UserPrediction`
						SET `ResolvedPredictionID`=?
						WHERE `ResolvedPredictionID`='' AND `ChannelID`=?""")
		){
			conn.setAutoCommit(false);
		 
			try{
				//Get user predictions, determine win/lose and update
                double returnOnInvestment = returnRatioForWin - 1;
				getOpenPredictionStmt.setString(1, channelId);
				try(ResultSet result = getOpenPredictionStmt.executeQuery()){
					while(result.next()){
						var userPrediction = Converters.convertUserPrediction(result);
                        if(badge.equals(userPrediction.getBadge())){
                            updatePredictionUserStmt.setInt(1, 1);
                            updatePredictionUserStmt.setDouble(2, returnOnInvestment);
                        }
                        else {
                            updatePredictionUserStmt.setInt(1, 0);
                            updatePredictionUserStmt.setDouble(2, -1);
                        }
						updatePredictionUserStmt.setInt(3, userPrediction.getUserId());
						updatePredictionUserStmt.addBatch();
					}
					updatePredictionUserStmt.executeBatch();
				}
	
				//Add the resolved prediction
				addResolvedPredictionStmt.setString(1, eventId);
				addResolvedPredictionStmt.setString(2, channelId);
				addResolvedPredictionStmt.setString(3, title);
				addResolvedPredictionStmt.setObject(4, LocalDateTime.ofInstant(eventCreated, UTC));
				addResolvedPredictionStmt.setObject(5, LocalDateTime.ofInstant(eventEnded, UTC));
				addResolvedPredictionStmt.setString(6, outcome);
				addResolvedPredictionStmt.setString(7, badge);
				addResolvedPredictionStmt.setDouble(8, returnRatioForWin);
				addResolvedPredictionStmt.executeUpdate();
	
				//Update made predictions with event-id
				updateUserPredictionsStmt.setString(1, eventId);
				updateUserPredictionsStmt.setString(2, channelId);
				updateUserPredictionsStmt.executeUpdate();
			}
			catch(SQLException e){
				conn.rollback();
				throw e;
			}
			conn.commit();
		}
	}
    
    protected abstract PreparedStatement getUpdatePredictionUserStmt(Connection conn)  throws SQLException;
	@Override
	public void deleteUnresolvedUserPredictions() throws SQLException{
		log.debug("Removing all unresolved user predictions.");
		try(var conn = getConnection(); var statement = conn.prepareStatement("""
						DELETE FROM `UserPrediction`
						WHERE `ResolvedPredictionID`=''"""
		)){
			statement.executeUpdate();
		}
	}
	
	@Override
	public void deleteUnresolvedUserPredictionsForChannel(@NotNull String channelId) throws SQLException{
		log.debug("Removing unresolved user predictions for channelId '{}'.", channelId);
		try(var conn = getConnection(); var statement = conn.prepareStatement("""
						DELETE FROM `UserPrediction`
						WHERE `ResolvedPredictionID`='' AND `ChannelID`=?"""
		)){
			statement.setString(1, channelId);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	@NotNull
	public List<OutcomeStatistic> getOutcomeStatisticsForChannel(@NotNull String channelId, int minBetsPlacedByUser) throws SQLException{
		log.debug("Getting most trusted prediction from already placed bets.");
		try(var conn = getConnection(); var statement = conn.prepareStatement("""
						SELECT `Badge`,
							COUNT(`UserID`) AS UserCnt,
							AVG(`WinRate`) AS AvgWinRate,
							AVG(`PredictionCnt`) AS AvgUserBetsPlaced,
							AVG(`WinCnt`) AS AvgUserWins,
							AVG(`ReturnOnInvestment`) AS AvgReturnOnInvestment
						FROM `UserPrediction` AS up INNER JOIN `PredictionUser` AS pu ON up.`UserID`=pu.`ID`
						WHERE `ChannelID`=? AND `ResolvedPredictionID`='' AND `PredictionCnt`>? GROUP BY `Badge`"""
		)){
			statement.setString(1, channelId);
			statement.setInt(2, minBetsPlacedByUser);
			
			List<OutcomeStatistic> outcomeStatistics = new LinkedList<>();
			try(ResultSet result = statement.executeQuery()){
				while(result.next())
					outcomeStatistics.add(Converters.convertOutcomeTrust(result));
			}
			
			return outcomeStatistics;
		}
	}
	
	@Override
	public void close(){
		dataSource.close();
	}
	
	@NotNull
	@Override
	public Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}
	
	protected void execute(@NotNull String... statements) throws SQLException{
		try(var conn = getConnection()){
			conn.setAutoCommit(false);
			for(var sql : statements){
				try(var statement = conn.createStatement()){
					statement.execute(sql);
				}
				catch(SQLException e){
					conn.rollback();
					throw e;
				}
			}
			conn.commit();
		}
	}
}
