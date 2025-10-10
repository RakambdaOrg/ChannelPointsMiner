package fr.rakambda.channelpointsminer.miner.database;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.database.converter.Converters;
import fr.rakambda.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@Log4j2
public class PostgreSqlDatabase extends BaseDatabase{
	public PostgreSqlDatabase(DataSource dataSource){
		super(dataSource);
	}
	
	@Override
	public void initDatabase(){
		applyFlyway("db/migrations/postgresql");
	}
	
	@Override
	protected void addUserPrediction(@NonNull String channelId, int userId, @NonNull String badge) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO UserPrediction(ChannelID, UserID, Badge)
						VALUES (?, ?, ?)
						ON CONFLICT (ChannelID, UserID) DO NOTHING;
						""")){
			
			statement.setString(1, channelId);
			statement.setInt(2, userId);
			statement.setString(3, badge);
			statement.executeUpdate();
		}
	}
	
	@Override
	protected void resolveUserPredictions(double returnRatioForWin, @NonNull String channelId, @NonNull String badge) throws SQLException{
		try(var conn = getConnection();
				var getOpenPredictionStmt = conn.prepareStatement("""
						SELECT UserID, ChannelID, Badge
						FROM UserPrediction
						WHERE ChannelID = ?;
						""");
				var updatePredictionUserStmt = conn.prepareStatement("""
						UPDATE PredictionUser
						SET
						    PredictionCnt = PredictionCnt + 1,
						    WinCnt = WinCnt + ?,
						    WinRate = (WinCnt + ?)::FLOAT / (PredictionCnt + 1),
						    ReturnOnInvestment = ReturnOnInvestment + ?
						WHERE ID = ? AND ChannelID = ?;
						""")){
			
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
	
	@Override
	public void createChannel(@NonNull String channelId, @NonNull String username) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO Channel(ID, Username, LastStatusChange)
						VALUES (?, ?, NOW())
						ON CONFLICT (ID) DO NOTHING;
						""")){
			
			statement.setString(1, channelId);
			statement.setString(2, username);
			statement.executeUpdate();
		}
	}
	
	@Override
	public void deleteAllUserPredictions() throws SQLException{
		log.debug("Removing all user predictions.");
		try(var conn = getConnection();
				var statement = conn.prepareStatement("DELETE FROM UserPrediction")){
			statement.executeUpdate();
		}
	}
	
	@Override
	public void deleteUserPredictionsForChannel(@NonNull String channelId) throws SQLException{
		log.debug("Removing user predictions for channelId '{}'.", channelId);
		try(var conn = getConnection();
				var statement = getDeleteUserPredictionsForChannelStmt(conn)){
			statement.setString(1, channelId);
			
			statement.executeUpdate();
		}
	}
	
	@NonNull
	private PreparedStatement getDeleteUserPredictionsForChannelStmt(@NonNull Connection conn) throws SQLException{
		return conn.prepareStatement("""
				DELETE FROM UserPrediction
				WHERE ChannelID=?;"""
		);
	}
	
	@Override
	public @NonNull Optional<String> getStreamerIdFromName(@NonNull String channelName) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("SELECT ID FROM Channel WHERE Username = ?;")){
			statement.setString(1, channelName);
			try(var result = statement.executeQuery()){
				if(result.next()){
					return Optional.ofNullable(result.getString("ID"));
				}
				return Optional.empty();
			}
		}
	}
	
	@Override
	public @NonNull Collection<OutcomeStatistic> getOutcomeStatisticsForChannel(@NonNull String channelId, int minBetsPlacedByUser) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						SELECT up.Badge,
						       COUNT(up.UserID) AS UserCnt,
						       AVG(pu.WinRate) AS "AvgWinRate",
						       AVG(pu.PredictionCnt) AS AvgUserBetsPlaced,
						       AVG(pu.WinCnt) AS AvgUserWins,
						       AVG(pu.ReturnOnInvestment) AS AvgReturnOnInvestment
						FROM UserPrediction up
						INNER JOIN PredictionUser pu
						    ON up.UserID = pu.ID AND up.ChannelID = pu.ChannelID
						WHERE up.ChannelID = ?
						  AND pu.PredictionCnt >= ?
						GROUP BY up.Badge;
						""")){
			statement.setString(1, channelId);
			statement.setInt(2, minBetsPlacedByUser);
			
			var results = new LinkedList<OutcomeStatistic>();
			try(var rs = statement.executeQuery()){
				while(rs.next()){
					results.add(Converters.convertOutcomeTrust(rs));
				}
			}
			return results;
		}
	}
	
	@Override
	public void resolvePrediction(@NonNull Event event, @NonNull String outcome, @NonNull String badge, double returnRatioForWin) throws SQLException{
		var ended = Optional.ofNullable(event.getEndedAt()).map(ZonedDateTime::toInstant).orElseGet(TimeFactory::now);
		
		resolveUserPredictions(returnRatioForWin, event.getChannelId(), badge);
		
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO ResolvedPrediction(EventID, ChannelID,Title,EventCreated, EventEnded, Canceled,Outcome, Badge, ReturnRatioForWin)
						VALUES (?, ?, ?, ?, ?, false, ?, ?, ?)
						ON CONFLICT (EventID) DO NOTHING;
						""")){
			
			statement.setString(1, event.getId());
			statement.setString(2, event.getChannelId());
			statement.setString(3, event.getTitle());
			statement.setTimestamp(4, Timestamp.from(event.getCreatedAt().toInstant()));
			statement.setTimestamp(5, Timestamp.from(ended));
			statement.setString(6, outcome);
			statement.setString(7, badge);
			statement.setDouble(8, returnRatioForWin);
			statement.executeUpdate();
		}
		
		deleteUserPredictionsForChannel(event.getChannelId());
	}
	
	@Override
	public void cancelPrediction(@NonNull Event event) throws SQLException{
		var ended = Optional.ofNullable(event.getEndedAt()).map(ZonedDateTime::toInstant).orElseGet(TimeFactory::now);
		
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO ResolvedPrediction(EventID, ChannelID, Title, EventCreated, EventEnded, Canceled)
						VALUES (?, ?, ?, ?, ?, true)
						ON CONFLICT (EventID) DO NOTHING;
						""")){
			statement.setString(1, event.getId());
			statement.setString(2, event.getChannelId());
			statement.setString(3, event.getTitle());
			statement.setTimestamp(4, Timestamp.from(event.getCreatedAt().toInstant()));
			statement.setTimestamp(5, Timestamp.from(ended));
			statement.executeUpdate();
		}
		
		deleteUserPredictionsForChannel(event.getChannelId());
	}
	
	@Override
	public void addPrediction(@NonNull String channelId, @NonNull String eventId, @NonNull String type, @NonNull String description, @NonNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO Prediction(ChannelID, EventID, EventDate, Type, Description)
						VALUES (?, ?, ?, ?, ?);
						""")){
			statement.setString(1, channelId);
			statement.setString(2, eventId);
			statement.setTimestamp(3, Timestamp.from(instant));
			statement.setString(4, type);
			statement.setString(5, description);
			statement.executeUpdate();
		}
	}
	
	@Override
	public void addBalance(@NonNull String channelId, int balance, @Nullable String reason, @NonNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO Balance(ChannelID, BalanceDate, Balance, Reason)
						VALUES (?, ?, ?, ?);
						""")){
			statement.setString(1, channelId);
			statement.setTimestamp(2, Timestamp.from(instant));
			statement.setInt(3, balance);
			statement.setString(4, reason);
			statement.executeUpdate();
		}
	}
	
	@Override
	public void updateChannelStatusTime(@NonNull String channelId, @NonNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						UPDATE Channel
						SET LastStatusChange = ?
						WHERE ID = ?;
						""")){
			statement.setTimestamp(1, Timestamp.from(instant));
			statement.setString(2, channelId);
			statement.executeUpdate();
		}
	}
}
