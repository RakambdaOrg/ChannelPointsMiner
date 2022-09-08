package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.raksrinana.channelpointsminer.miner.database.converter.Converters;
import fr.raksrinana.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Log4j2
public abstract class BaseDatabase implements IDatabase{
	private final HikariDataSource dataSource;
	private final Lock[] getOrCreatePredictionUserIdLocks = new Lock[]{
			new ReentrantLock(),
			new ReentrantLock(),
			new ReentrantLock()
	};
	
	protected void applyFlyway(@NotNull String... migrationsPaths){
		var flyway = Flyway.configure()
				.dataSource(dataSource)
				.locations(migrationsPaths)
				.baselineOnMigrate(true)
				.baselineVersion("1")
				.load();
		flyway.migrate();
	}
	
	@Override
	public void updateChannelStatusTime(@NotNull String channelId, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						UPDATE `Channel` SET
						`LastStatusChange` = ?
						WHERE `ID` = ?;"""
				)){
			
			statement.setTimestamp(1, Timestamp.from(instant));
			statement.setString(2, channelId);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	public int addUserPrediction(@NotNull String username, @NotNull String channelId, @NotNull String badge) throws SQLException{
		var userId = getOrCreatePredictionUserId(username, channelId);
		addUserPrediction(channelId, userId, badge);
		return userId;
	}
	
	@Override
	public void addBalance(@NotNull String channelId, int balance, @Nullable String reason, @NotNull Instant instant) throws SQLException{
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						INSERT INTO `Balance`(`ChannelId`, `BalanceDate`, `Balance`, `Reason`)
						VALUES(?, ?, ?, ?);"""
				)){
			
			statement.setString(1, channelId);
			statement.setTimestamp(2, Timestamp.from(instant));
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
			statement.setTimestamp(3, Timestamp.from(instant));
			statement.setString(4, type);
			statement.setString(5, description);
			
			statement.executeUpdate();
		}
	}
	
	protected int getOrCreatePredictionUserId(@NotNull String username, @NotNull String channelId) throws SQLException{
		username = username.toLowerCase(Locale.ROOT);
		var lock = getOrCreatePredictionUserIdLocks[hashToIndex(username.hashCode(), getOrCreatePredictionUserIdLocks.length)];
		lock.lock();
		
		try(var conn = getConnection();
				var selectUserStatement = conn.prepareStatement("""
						SELECT `ID` FROM `PredictionUser` WHERE `Username`=? AND `ChannelID`=?""")){
			
			selectUserStatement.setString(1, username);
			selectUserStatement.setString(2, channelId);
			var userResult = selectUserStatement.executeQuery();
			
			if(userResult.next()){
				return userResult.getInt(1);
			}
			
			try(var addUserStatement = conn.prepareStatement("""
					INSERT INTO `PredictionUser`(`Username`, `ChannelID`) VALUES (?, ?)""", Statement.RETURN_GENERATED_KEYS)){
				addUserStatement.setString(1, username);
				addUserStatement.setString(2, channelId);
				var insertResult = addUserStatement.executeUpdate();
				if(insertResult <= 0){
					throw new SQLException("Failed to create new prediction user");
				}
				
				var generatedKeys = addUserStatement.getGeneratedKeys();
				if(!generatedKeys.next()){
					throw new SQLException("Failed to get new prediction user id");
				}
				
				var userId = generatedKeys.getInt(1);
				log.debug("Added new prediction user '{}' for channel '{}' : {}", username, channelId, userId);
				return userId;
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	protected abstract void addUserPrediction(@NotNull String channelId, int userId, @NotNull String badge) throws SQLException;
	
	private int hashToIndex(int hash, int length){
		if(hash == Integer.MIN_VALUE){
			return 0;
		}
		return Math.abs(hash) % length;
	}
	
	@Override
	public void cancelPrediction(@NotNull Event event) throws SQLException{
		var ended = Optional.ofNullable(event.getEndedAt()).map(ZonedDateTime::toInstant).orElseGet(TimeFactory::now);
		
		try(var conn = getConnection();
				var addCanceledPredictionStmt = conn.prepareStatement("""
						INSERT INTO `ResolvedPrediction`(`EventID`,`ChannelID`, `Title`,`EventCreated`,`EventEnded`,`Canceled`)
						VALUES (?,?,?,?,?,true)""")
		){
			addCanceledPredictionStmt.setString(1, event.getId());
			addCanceledPredictionStmt.setString(2, event.getChannelId());
			addCanceledPredictionStmt.setString(3, event.getTitle());
			addCanceledPredictionStmt.setTimestamp(4, Timestamp.from(event.getCreatedAt().toInstant()));
			addCanceledPredictionStmt.setTimestamp(5, Timestamp.from(ended));
			addCanceledPredictionStmt.executeUpdate();
		}
		
		deleteUserPredictionsForChannel(event.getChannelId());
	}
	
	@Override
	public void resolvePrediction(@NotNull Event event, @NotNull String outcome, @NotNull String badge, double returnRatioForWin) throws SQLException{
		var ended = Optional.ofNullable(event.getEndedAt()).map(ZonedDateTime::toInstant).orElseGet(TimeFactory::now);
		
		resolveUserPredictions(returnRatioForWin, event.getChannelId(), badge);
		
		try(var conn = getConnection();
				var addResolvedPredictionStmt = conn.prepareStatement("""
						INSERT INTO `ResolvedPrediction`(`EventID`,`ChannelID`, `Title`,`EventCreated`,`EventEnded`,`Canceled`,`Outcome`,`Badge`,`ReturnRatioForWin`)
						VALUES (?,?,?,?,?,false,?,?,?)""")
		){
			addResolvedPredictionStmt.setString(1, event.getId());
			addResolvedPredictionStmt.setString(2, event.getChannelId());
			addResolvedPredictionStmt.setString(3, event.getTitle());
			addResolvedPredictionStmt.setTimestamp(4, Timestamp.from(event.getCreatedAt().toInstant()));
			addResolvedPredictionStmt.setTimestamp(5, Timestamp.from(ended));
			addResolvedPredictionStmt.setString(6, outcome);
			addResolvedPredictionStmt.setString(7, badge);
			addResolvedPredictionStmt.setDouble(8, returnRatioForWin);
			addResolvedPredictionStmt.executeUpdate();
		}
		
		deleteUserPredictionsForChannel(event.getChannelId());
	}
	
	protected abstract void resolveUserPredictions(double returnRatioForWin, @NotNull String channelId, @NotNull String badge) throws SQLException;
	
	@Override
	public void deleteAllUserPredictions() throws SQLException{
		log.debug("Removing all user predictions.");
		try(var conn = getConnection();
				var statement = conn.prepareStatement("DELETE FROM `UserPrediction`")){
			statement.executeUpdate();
		}
	}
	
	@Override
	public void deleteUserPredictionsForChannel(@NotNull String channelId) throws SQLException{
		log.debug("Removing user predictions for channelId '{}'.", channelId);
		try(var conn = getConnection(); var statement = getDeleteUserPredictionsForChannelStmt(conn)){
			statement.setString(1, channelId);
			
			statement.executeUpdate();
		}
	}
	
	@Override
	@NotNull
	public Optional<String> getStreamerIdFromName(@NotNull String channelName) throws SQLException{
		log.debug("Getting streamerId from channel {}", channelName);
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						SELECT `ID`
						FROM `Channel`
						WHERE `Username`=?"""
				)){
			statement.setString(1, channelName);
			
			try(var result = statement.executeQuery()){
				if(result.next()){
					return Optional.ofNullable(result.getString("ID"));
				}
			}
			
			return Optional.empty();
		}
	}
	
	@Override
	@NotNull
	public Collection<OutcomeStatistic> getOutcomeStatisticsForChannel(@NotNull String channelId, int minBetsPlacedByUser) throws SQLException{
		log.debug("Getting most trusted prediction from already placed bets.");
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						SELECT `Badge`,
							COUNT(`UserID`) AS UserCnt,
							AVG(`WinRate`) AS AvgWinRate,
							AVG(`PredictionCnt`) AS AvgUserBetsPlaced,
							AVG(`WinCnt`) AS AvgUserWins,
							AVG(`ReturnOnInvestment`) AS AvgReturnOnInvestment
						FROM `UserPrediction` AS up
						INNER JOIN `PredictionUser` AS pu
						ON up.`UserID`=pu.`ID` AND up.`ChannelID` = pu.`ChannelID`
						WHERE up.`ChannelID`=?
						AND `PredictionCnt`>=?
						GROUP BY `Badge`"""
				)){
			statement.setString(1, channelId);
			statement.setInt(2, minBetsPlacedByUser);
			
			var outcomeStatistics = new LinkedList<OutcomeStatistic>();
			try(var result = statement.executeQuery()){
				while(result.next()){
					outcomeStatistics.add(Converters.convertOutcomeTrust(result));
				}
			}
			
			return outcomeStatistics;
		}
	}
	
	@Override
	public void close(){
		dataSource.close();
	}
	
	@NotNull
	protected Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}
	
	@NotNull
	private PreparedStatement getDeleteUserPredictionsForChannelStmt(@NotNull Connection conn) throws SQLException{
		return conn.prepareStatement("""
				DELETE FROM `UserPrediction`
				WHERE `ChannelID`=?"""
		);
	}
}
