package fr.rakambda.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.Event;
import fr.rakambda.channelpointsminer.miner.database.model.prediction.OutcomeStatistic;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import org.assertj.core.api.Assertions;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Table;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQLiteDatabaseTest{
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_USERNAME = "channel-username";
	private static final String USER_USERNAME = "user1";
	private static final String EVENT_ID = "event-id";
	private static final String EVENT_TITLE = "event-title";
	private static final ZonedDateTime EVENT_CREATED_AT = ZonedDateTime.of(2022, 2, 15, 12, 0, 0, 0, ZoneId.systemDefault());
	private static final ZonedDateTime EVENT_ENDED_AT = EVENT_CREATED_AT.plusDays(1);
	
	private static final String ID_COL = "ID";
	private static final String USERNAME_COL = "Username";
	private static final String LAST_STATUS_CHANGE_COL = "LastStatusChange";
	private static final String CHANNEL_ID_COL = "ChannelID";
	private static final String BALANCE_DATE_COL = "BalanceDate";
	private static final String BALANCE_COL = "Balance";
	private static final String REASON_COL = "Reason";
	private static final String EVENT_ID_COL = "EventID";
	private static final String EVENT_DATE_COL = "EventDate";
	private static final String TYPE_COL = "Type";
	private static final String DESCRIPTION_COL = "Description";
	private static final String PREDICTION_CNT_COL = "PredictionCnt";
	private static final String WIN_CNT_COL = "WinCnt";
	private static final String WIN_RATE_COL = "WinRate";
	private static final String RETURN_ON_INVESTMENT_COL = "ReturnOnInvestment";
	private static final String USER_ID_COL = "UserID";
	private static final String BADGE_COL = "Badge";
	private static final String TITLE_COL = "Title";
	private static final String EVENT_CREATED_COL = "EventCreated";
	private static final String EVENT_ENDED_COL = "EventEnded";
	private static final String CANCELED_COL = "Canceled";
	private static final String OUTCOME_COL = "Outcome";
	private static final String RETURN_RATIO_FOR_WIN_COL = "ReturnRatioForWIn";
	
	@TempDir
	private Path tempPath;
	@Mock
	private Event event;
	
	private SQLiteDatabase tested;
	private HikariDataSource dataSource;
	
	private Supplier<Changes> changesBalance;
	private Supplier<Changes> changesChannel;
	private Supplier<Changes> changesPrediction;
	private Supplier<Changes> changesPredictionUser;
	private Supplier<Changes> changesUserPrediction;
	private Supplier<Changes> changesResolvedPrediction;
	
	@BeforeEach
	void setUp() throws SQLException{
		var poolConfiguration = new HikariConfig();
		poolConfiguration.setJdbcUrl("jdbc:sqlite:" + tempPath.resolve(System.currentTimeMillis() + "_test.db").toAbsolutePath());
		poolConfiguration.setDriverClassName("org.sqlite.JDBC");
		poolConfiguration.setMaximumPoolSize(1);
		
		dataSource = new HikariDataSource(poolConfiguration);
		tested = new SQLiteDatabase(dataSource);
		
		tested.initDatabase();
		
		changesBalance = () -> new Changes(new Table(dataSource, "Balance"));
		changesChannel = () -> new Changes(new Table(dataSource, "Channel"));
		changesPrediction = () -> new Changes(new Table(dataSource, "Prediction"));
		changesPredictionUser = () -> new Changes(new Table(dataSource, "PredictionUser"));
		changesUserPrediction = () -> new Changes(new Table(dataSource, "UserPrediction"));
		changesResolvedPrediction = () -> new Changes(new Table(dataSource, "ResolvedPrediction"));
		
		lenient().when(event.getId()).thenReturn(EVENT_ID);
		lenient().when(event.getChannelId()).thenReturn(CHANNEL_ID);
		lenient().when(event.getTitle()).thenReturn(EVENT_TITLE);
		lenient().when(event.getCreatedAt()).thenReturn(EVENT_CREATED_AT);
		lenient().when(event.getEndedAt()).thenReturn(EVENT_ENDED_AT);
	}
	
	@AfterEach
	void tearDown(){
		tested.close();
	}
	
	@Test
	void tablesAreCreated(){
		assertThat(new Table(dataSource, "Balance")).exists();
		assertThat(new Table(dataSource, "Channel")).exists();
		assertThat(new Table(dataSource, "Prediction")).exists();
		assertThat(new Table(dataSource, "PredictionUser")).exists();
		assertThat(new Table(dataSource, "UserPrediction")).exists();
	}
	
	@Test
	void newChannelIsInsertedIfNew() throws SQLException{
		var changes = changesChannel.get();
		
		changes.setStartPointNow();
		tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(USERNAME_COL).valueAtEndPoint().isEqualTo(CHANNEL_USERNAME)
				.column(LAST_STATUS_CHANGE_COL).valueAtEndPoint().isNotNull();
	}
	
	@Test
	void oldChannelIsNotInsertedIfNew() throws SQLException{
		try(var factory = mockStatic(TimeFactory.class)){
			var changes = changesChannel.get();
			
			var beforeChange = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
			factory.when(TimeFactory::now).thenReturn(beforeChange);
			
			tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
			
			factory.when(TimeFactory::now).thenReturn(beforeChange.plus(1, ChronoUnit.HOURS));
			
			changes.setStartPointNow();
			tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
			changes.setEndPointNow();
			
			assertThat(changes).hasNumberOfChanges(0);
		}
	}
	
	@Test
	void updateChannelStatusTime() throws SQLException{
		try(var factory = mockStatic(TimeFactory.class)){
			var changes = changesChannel.get();
			
			var beforeChange = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
			factory.when(TimeFactory::now).thenReturn(beforeChange);
			
			tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
			
			var newTime = beforeChange.plus(1, ChronoUnit.HOURS);
			
			changes.setStartPointNow();
			tested.updateChannelStatusTime(CHANNEL_ID, newTime);
			changes.setEndPointNow();
			
			assertThat(changes).hasNumberOfChanges(1)
					.changeOfModification()
					.column(LAST_STATUS_CHANGE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(newTime));
		}
	}
	
	@Test
	void getStreamerIdFromName() throws SQLException{
		tested.createChannel("other-id", "other-name");
		
		Assertions.assertThat(tested.getStreamerIdFromName(CHANNEL_USERNAME)).isEmpty();
		
		tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
		
		Assertions.assertThat(tested.getStreamerIdFromName(CHANNEL_USERNAME)).isPresent().get().isEqualTo(CHANNEL_ID);
	}
	
	@Test
	void addBalance() throws SQLException{
		var changes = changesBalance.get();
		
		var firstInstant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
		changes.setStartPointNow();
		tested.addBalance(CHANNEL_ID, 25, "Test1", firstInstant);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(BALANCE_DATE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(firstInstant))
				.column(BALANCE_COL).valueAtEndPoint().isEqualTo(25)
				.column(REASON_COL).valueAtEndPoint().isEqualTo("Test1");
		
		var secondInstant = firstInstant.plusSeconds(30);
		changes.setStartPointNow();
		tested.addBalance(CHANNEL_ID, 50, "Test2", secondInstant);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(BALANCE_DATE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(secondInstant))
				.column(BALANCE_COL).valueAtEndPoint().isEqualTo(50)
				.column(REASON_COL).valueAtEndPoint().isEqualTo("Test2");
	}
	
	@Test
	void addPrediction() throws SQLException{
		var changes = changesPrediction.get();
		
		var firstInstant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
		changes.setStartPointNow();
		tested.addPrediction(CHANNEL_ID, "Event1", "Type1", "Description1", firstInstant);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(EVENT_ID_COL).valueAtEndPoint().isEqualTo("Event1")
				.column(EVENT_DATE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(firstInstant))
				.column(TYPE_COL).valueAtEndPoint().isEqualTo("Type1")
				.column(DESCRIPTION_COL).valueAtEndPoint().isEqualTo("Description1");
		
		var secondInstant = firstInstant.plusSeconds(30);
		changes.setStartPointNow();
		tested.addPrediction(CHANNEL_ID, "Event2", "Type2", "Description2", secondInstant);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(EVENT_ID_COL).valueAtEndPoint().isEqualTo("Event2")
				.column(EVENT_DATE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(secondInstant))
				.column(TYPE_COL).valueAtEndPoint().isEqualTo("Type2")
				.column(DESCRIPTION_COL).valueAtEndPoint().isEqualTo("Description2");
	}
	
	@Test
	void addPredictionFromNewUser() throws SQLException{
		var changesUserPrediction = this.changesUserPrediction.get();
		var changesPredictionUser = this.changesPredictionUser.get();
		
		changesUserPrediction.setStartPointNow();
		changesPredictionUser.setStartPointNow();
		var userId = tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		changesUserPrediction.setEndPointNow();
		changesPredictionUser.setEndPointNow();
		
		assertThat(changesPredictionUser).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isEqualTo(userId)
				.column(USERNAME_COL).valueAtEndPoint().isEqualTo(USER_USERNAME)
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(PREDICTION_CNT_COL).valueAtEndPoint().isEqualTo(0)
				.column(WIN_CNT_COL).valueAtEndPoint().isEqualTo(0)
				.column(WIN_RATE_COL).valueAtEndPoint().isEqualTo(0D)
				.column(RETURN_ON_INVESTMENT_COL).valueAtEndPoint().isEqualTo(0D);
		
		assertThat(changesUserPrediction).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(USER_ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(BADGE_COL).valueAtEndPoint().isEqualTo("B1");
	}
	
	@Test
	void addPredictionFromExistingUser() throws SQLException{
		var changesUserPrediction = this.changesUserPrediction.get();
		var changesPredictionUser = this.changesPredictionUser.get();
		
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		
		changesUserPrediction.setStartPointNow();
		changesPredictionUser.setStartPointNow();
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B2"); //This should be impossible, we can't change badge
		changesUserPrediction.setEndPointNow();
		changesPredictionUser.setEndPointNow();
		
		assertThat(changesPredictionUser).hasNumberOfChanges(0);
		assertThat(changesUserPrediction).hasNumberOfChanges(0);
	}
	
	@Test
	void cancelPrediction() throws SQLException{
		var changes = changesResolvedPrediction.get();
		
		changes.setStartPointNow();
		tested.cancelPrediction(event);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(EVENT_ID_COL).valueAtEndPoint().isEqualTo(EVENT_ID)
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(TITLE_COL).valueAtEndPoint().isEqualTo(EVENT_TITLE)
				.column(EVENT_CREATED_COL).valueAtEndPoint().isEqualTo(EVENT_CREATED_AT.toLocalDateTime())
				.column(EVENT_ENDED_COL).valueAtEndPoint().isEqualTo(EVENT_ENDED_AT.toLocalDateTime())
				.column(CANCELED_COL).valueAtEndPoint().isEqualTo(1)
				.column(OUTCOME_COL).valueAtEndPoint().isNull()
				.column(BADGE_COL).valueAtEndPoint().isNull()
				.column(RETURN_RATIO_FOR_WIN_COL).valueAtEndPoint().isNull();
	}
	
	@Test
	void cancelPredictionWithNoEndDate() throws SQLException{
		try(var factory = mockStatic(TimeFactory.class)){
			var endInstant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
			factory.when(TimeFactory::now).thenReturn(endInstant);
			
			var changes = changesResolvedPrediction.get();
			
			when(event.getEndedAt()).thenReturn(null);
			
			changes.setStartPointNow();
			tested.cancelPrediction(event);
			changes.setEndPointNow();
			
			assertThat(changes).hasNumberOfChanges(1)
					.changeOfCreation()
					.column(EVENT_ID_COL).valueAtEndPoint().isEqualTo(EVENT_ID)
					.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
					.column(TITLE_COL).valueAtEndPoint().isEqualTo(EVENT_TITLE)
					.column(EVENT_CREATED_COL).valueAtEndPoint().isEqualTo(EVENT_CREATED_AT.toLocalDateTime())
					.column(EVENT_ENDED_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(endInstant))
					.column(CANCELED_COL).valueAtEndPoint().isEqualTo(1)
					.column(OUTCOME_COL).valueAtEndPoint().isNull()
					.column(BADGE_COL).valueAtEndPoint().isNull()
					.column(RETURN_RATIO_FOR_WIN_COL).valueAtEndPoint().isNull();
		}
	}
	
	@Test
	void cancelPredictionClearsUserPredictions() throws SQLException{
		var changes = changesUserPrediction.get();
		
		var userId1 = tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		var userId2 = tested.addUserPrediction("user-2", CHANNEL_ID, "B2");
		tested.addUserPrediction(USER_USERNAME, "other-channel", "B1");
		
		changes.setStartPointNow();
		tested.cancelPrediction(event);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(2)
				.changeOfDeletion()
				.column(USER_ID_COL).valueAtStartPoint().isEqualTo(userId1)
				.column(CHANNEL_ID_COL).valueAtStartPoint().isEqualTo(CHANNEL_ID)
				.changeOfDeletion()
				.column(USER_ID_COL).valueAtStartPoint().isEqualTo(userId2)
				.column(CHANNEL_ID_COL).valueAtStartPoint().isEqualTo(CHANNEL_ID);
	}
	
	@Test
	void resolvePrediction() throws SQLException{
		var changes = changesResolvedPrediction.get();
		
		changes.setStartPointNow();
		tested.resolvePrediction(event, "Outcome1", "B1", 1.5D);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(EVENT_ID_COL).valueAtEndPoint().isEqualTo(EVENT_ID)
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(TITLE_COL).valueAtEndPoint().isEqualTo(EVENT_TITLE)
				.column(EVENT_CREATED_COL).valueAtEndPoint().isEqualTo(EVENT_CREATED_AT.toLocalDateTime())
				.column(EVENT_ENDED_COL).valueAtEndPoint().isEqualTo(EVENT_ENDED_AT.toLocalDateTime())
				.column(CANCELED_COL).valueAtEndPoint().isEqualTo(0)
				.column(OUTCOME_COL).valueAtEndPoint().isEqualTo("Outcome1")
				.column(BADGE_COL).valueAtEndPoint().isEqualTo("B1")
				.column(RETURN_RATIO_FOR_WIN_COL).valueAtEndPoint().isEqualTo(1.5D);
	}
	
	@Test
	void resolvePredictionWithNoEndDate() throws SQLException{
		try(var factory = mockStatic(TimeFactory.class)){
			var endInstant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
			factory.when(TimeFactory::now).thenReturn(endInstant);
			
			var changes = changesResolvedPrediction.get();
			
			when(event.getEndedAt()).thenReturn(null);
			
			changes.setStartPointNow();
			tested.resolvePrediction(event, "Outcome1", "B1", 1.5D);
			changes.setEndPointNow();
			
			assertThat(changes).hasNumberOfChanges(1)
					.changeOfCreation()
					.column(EVENT_ID_COL).valueAtEndPoint().isEqualTo(EVENT_ID)
					.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
					.column(TITLE_COL).valueAtEndPoint().isEqualTo(EVENT_TITLE)
					.column(EVENT_CREATED_COL).valueAtEndPoint().isEqualTo(EVENT_CREATED_AT.toLocalDateTime())
					.column(EVENT_ENDED_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(endInstant))
					.column(CANCELED_COL).valueAtEndPoint().isEqualTo(0)
					.column(OUTCOME_COL).valueAtEndPoint().isEqualTo("Outcome1")
					.column(BADGE_COL).valueAtEndPoint().isEqualTo("B1")
					.column(RETURN_RATIO_FOR_WIN_COL).valueAtEndPoint().isEqualTo(1.5D);
		}
	}
	
	@Test
	void resolvePredictionClearsUserPredictions() throws SQLException{
		var changes = changesUserPrediction.get();
		
		var userId1 = tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		var userId2 = tested.addUserPrediction("user-2", CHANNEL_ID, "B2");
		tested.addUserPrediction(USER_USERNAME, "other-channel", "B1");
		
		changes.setStartPointNow();
		tested.resolvePrediction(event, "Outcome1", "B1", 1.5D);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(2)
				.changeOfDeletion()
				.column(USER_ID_COL).valueAtStartPoint().isEqualTo(userId1)
				.column(CHANNEL_ID_COL).valueAtStartPoint().isEqualTo(CHANNEL_ID)
				.changeOfDeletion()
				.column(USER_ID_COL).valueAtStartPoint().isEqualTo(userId2)
				.column(CHANNEL_ID_COL).valueAtStartPoint().isEqualTo(CHANNEL_ID);
	}
	
	@Test
	void resolvePredictionUpdatesPredictionUsers() throws SQLException{
		var changes = changesPredictionUser.get();
		
		var userId1 = tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		var userId2 = tested.addUserPrediction("user-2", CHANNEL_ID, "B2");
		
		changes.setStartPointNow();
		tested.resolvePrediction(event, "Outcome1", "B1", 1.5D);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(2)
				.changeOfModification()
				.column(ID_COL).valueAtEndPoint().isEqualTo(userId1)
				.column(USERNAME_COL).valueAtEndPoint().isEqualTo(USER_USERNAME)
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(PREDICTION_CNT_COL).valueAtEndPoint().isEqualTo(1)
				.column(WIN_CNT_COL).valueAtEndPoint().isEqualTo(1)
				.column(WIN_RATE_COL).valueAtEndPoint().isEqualTo(1D)
				.column(RETURN_ON_INVESTMENT_COL).valueAtEndPoint().isEqualTo(0.5D)
				.changeOfModification()
				.column(ID_COL).valueAtEndPoint().isEqualTo(userId2)
				.column(USERNAME_COL).valueAtEndPoint().isEqualTo("user-2")
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(PREDICTION_CNT_COL).valueAtEndPoint().isEqualTo(1)
				.column(WIN_CNT_COL).valueAtEndPoint().isEqualTo(0)
				.column(WIN_RATE_COL).valueAtEndPoint().isEqualTo(0D)
				.column(RETURN_ON_INVESTMENT_COL).valueAtEndPoint().isEqualTo(-1D);
	}
	
	@Test
	void getOutcomeStatisticsForChannel() throws SQLException{
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		tested.addUserPrediction("user-2", CHANNEL_ID, "B2");
		
		tested.resolvePrediction(event, "Outcome1", "B1", 1.5D);
		
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B3");
		tested.addUserPrediction("user-2", CHANNEL_ID, "B4");
		tested.addUserPrediction("user-3", CHANNEL_ID, "B5");
		
		Assertions.assertThat(tested.getOutcomeStatisticsForChannel(CHANNEL_ID, 1))
				.containsExactlyInAnyOrder(
						OutcomeStatistic.builder()
								.badge("B3")
								.userCnt(1)
								.averageWinRate(1D)
								.averageUserBetsPlaced(1D)
								.averageUserWins(1D)
								.averageReturnOnInvestment(0.5)
								.build(),
						OutcomeStatistic.builder()
								.badge("B4")
								.userCnt(1)
								.averageWinRate(0D)
								.averageUserBetsPlaced(1D)
								.averageUserWins(0D)
								.averageReturnOnInvestment(-1D)
								.build()
				);
	}
	
	@Test
	void deleteAllUserPredictions() throws SQLException{
		var changes = changesUserPrediction.get();
		
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		tested.addUserPrediction(USER_USERNAME, "channel-2", "B2");
		tested.addUserPrediction("user-2", CHANNEL_ID, "B2");
		
		changes.setStartPointNow();
		tested.deleteAllUserPredictions();
		changes.setEndPointNow();
		
		assertThat(changes).ofDeletion().hasNumberOfChanges(3);
	}
	
	@Test
	void deleteUserPredictionsForChannel() throws SQLException{
		var changes = changesUserPrediction.get();
		
		var userId1 = tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		tested.addUserPrediction(USER_USERNAME, "channel-2", "B2");
		var userId3 = tested.addUserPrediction("user-2", CHANNEL_ID, "B2");
		
		changes.setStartPointNow();
		tested.deleteUserPredictionsForChannel(CHANNEL_ID);
		changes.setEndPointNow();
		
		assertThat(changes).hasNumberOfChanges(2)
				.changeOfDeletion()
				.column(USER_ID_COL).valueAtStartPoint().isEqualTo(userId1)
				.column(CHANNEL_ID_COL).valueAtStartPoint().isEqualTo(CHANNEL_ID)
				.changeOfDeletion()
				.column(USER_ID_COL).valueAtStartPoint().isEqualTo(userId3)
				.column(CHANNEL_ID_COL).valueAtStartPoint().isEqualTo(CHANNEL_ID);
	}
	
	private LocalDateTime getExpectedTimestamp(Instant instant){
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
}