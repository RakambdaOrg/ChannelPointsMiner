package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import org.assertj.db.type.Changes;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class SQLiteDatabaseTest{
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_USERNAME = "channel-username";
	private static final String USER_USERNAME = "user1";
	
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
	
	@TempDir
	private Path tempPath;
	
	private SQLiteDatabase tested;
	private HikariDataSource dataSource;
	
	private final Supplier<Table> tableBalance = () -> new Table(dataSource, BALANCE_COL);
	private final Supplier<Table> tableChannel = () -> new Table(dataSource, "Channel");
	private final Supplier<Table> tablePrediction = () -> new Table(dataSource, "Prediction");
	private final Supplier<Table> tablePredictionUser = () -> new Table(dataSource, "PredictionUser");
	private final Supplier<Table> tableUserPrediction = () -> new Table(dataSource, "UserPrediction");
	
	@BeforeEach
	void setUp() throws SQLException{
		var poolConfiguration = new HikariConfig();
		poolConfiguration.setJdbcUrl("jdbc:sqlite:" + tempPath.resolve(System.currentTimeMillis() + "_test.db").toAbsolutePath());
		poolConfiguration.setDriverClassName("org.sqlite.JDBC");
		poolConfiguration.setMaximumPoolSize(1);
		
		dataSource = new HikariDataSource(poolConfiguration);
		tested = new SQLiteDatabase(dataSource);
		
		tested.initDatabase();
	}
	
	@AfterEach
	void tearDown(){
		tested.close();
	}
	
	@Test
	void tablesAreCreated(){
		assertThat(tableBalance.get()).exists();
		assertThat(tableChannel.get()).exists();
		assertThat(tablePrediction.get()).exists();
		assertThat(tablePredictionUser.get()).exists();
		assertThat(tableUserPrediction.get()).exists();
	}
	
	@Test
	void newChannelIsInsertedIfNew() throws SQLException{
		var table = tableChannel.get();
		var changes = new Changes(table);
		
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
			var table = tableChannel.get();
			var changes = new Changes(table);
			
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
			var table = tableChannel.get();
			var changes = new Changes(table);
			
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
	void addBalance() throws SQLException{
		var table = tableBalance.get();
		var change = new Changes(table);
		
		var firstInstant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
		change.setStartPointNow();
		tested.addBalance(CHANNEL_ID, 25, "Test1", firstInstant);
		change.setEndPointNow();
		
		assertThat(change).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(BALANCE_DATE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(firstInstant))
				.column(BALANCE_COL).valueAtEndPoint().isEqualTo(25)
				.column(REASON_COL).valueAtEndPoint().isEqualTo("Test1");
		
		var secondInstant = firstInstant.plusSeconds(30);
		change.setStartPointNow();
		tested.addBalance(CHANNEL_ID, 50, "Test2", secondInstant);
		change.setEndPointNow();
		
		assertThat(change).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(BALANCE_DATE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(secondInstant))
				.column(BALANCE_COL).valueAtEndPoint().isEqualTo(50)
				.column(REASON_COL).valueAtEndPoint().isEqualTo("Test2");
	}
	
	@Test
	void addPrediction() throws SQLException{
		var table = tablePrediction.get();
		var change = new Changes(table);
		
		var firstInstant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
		change.setStartPointNow();
		tested.addPrediction(CHANNEL_ID, "Event1", "Type1", "Description1", firstInstant);
		change.setEndPointNow();
		
		assertThat(change).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(EVENT_ID_COL).valueAtEndPoint().isEqualTo("Event1")
				.column(EVENT_DATE_COL).valueAtEndPoint().isEqualTo(getExpectedTimestamp(firstInstant))
				.column(TYPE_COL).valueAtEndPoint().isEqualTo("Type1")
				.column(DESCRIPTION_COL).valueAtEndPoint().isEqualTo("Description1");
		
		var secondInstant = firstInstant.plusSeconds(30);
		change.setStartPointNow();
		tested.addPrediction(CHANNEL_ID, "Event2", "Type2", "Description2", secondInstant);
		change.setEndPointNow();
		
		assertThat(change).hasNumberOfChanges(1)
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
		var tableUserPrediction = this.tableUserPrediction.get();
		var tablePredictionUser = this.tablePredictionUser.get();
		var changeUserPrediction = new Changes(tableUserPrediction);
		var changePredictionUser = new Changes(tablePredictionUser);
		
		changeUserPrediction.setStartPointNow();
		changePredictionUser.setStartPointNow();
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		changeUserPrediction.setEndPointNow();
		changePredictionUser.setEndPointNow();
		
		assertThat(changePredictionUser).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(ID_COL).valueAtEndPoint().isNotNull()
				.column(USERNAME_COL).valueAtEndPoint().isEqualTo(USER_USERNAME)
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(PREDICTION_CNT_COL).valueAtEndPoint().isEqualTo(0)
				.column(WIN_CNT_COL).valueAtEndPoint().isEqualTo(0)
				.column(WIN_RATE_COL).valueAtEndPoint().isEqualTo(0D)
				.column(RETURN_ON_INVESTMENT_COL).valueAtEndPoint().isEqualTo(0D);
		
		assertThat(changeUserPrediction).hasNumberOfChanges(1)
				.changeOfCreation()
				.column(USER_ID_COL).valueAtEndPoint().isNotNull()
				.column(CHANNEL_ID_COL).valueAtEndPoint().isEqualTo(CHANNEL_ID)
				.column(BADGE_COL).valueAtEndPoint().isEqualTo("B1");
	}
	
	@Test
	void addPredictionFromExistingUser() throws SQLException{
		var tableUserPrediction = this.tableUserPrediction.get();
		var tablePredictionUser = this.tablePredictionUser.get();
		var changeUserPrediction = new Changes(tableUserPrediction);
		var changePredictionUser = new Changes(tablePredictionUser);
		
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B1");
		
		changeUserPrediction.setStartPointNow();
		changePredictionUser.setStartPointNow();
		tested.addUserPrediction(USER_USERNAME, CHANNEL_ID, "B2"); //This should be impossible, we can't change badge
		changeUserPrediction.setEndPointNow();
		changePredictionUser.setEndPointNow();
		
		assertThat(changePredictionUser).hasNumberOfChanges(0);
		assertThat(changeUserPrediction).hasNumberOfChanges(0);
	}
	
	private LocalDateTime getExpectedTimestamp(Instant instant){
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
}