package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
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
	
	@TempDir
	private Path tempPath;
	
	private SQLiteDatabase tested;
	private HikariDataSource dataSource;
	
	private final Supplier<Table> tableBalance = () -> new Table(dataSource, "Balance");
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
		tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
		
		assertThat(tableChannel.get())
				.hasNumberOfRows(1)
				.row()
				.column("ID").value().isEqualTo(CHANNEL_ID)
				.column("Username").value().isEqualTo(CHANNEL_USERNAME)
				.column("LastStatusChange").value().isNotNull();
	}
	
	@Test
	void oldChannelIsNotInsertedIfNew() throws SQLException{
		try(var factory = mockStatic(TimeFactory.class)){
			var beforeChange = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
			factory.when(TimeFactory::now).thenReturn(beforeChange);
			
			tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
			assertThat(tableChannel.get()).hasNumberOfRows(1);
			
			factory.when(TimeFactory::now).thenReturn(beforeChange.plus(1, ChronoUnit.HOURS));
			
			tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
			
			assertThat(tableChannel.get()).hasNumberOfRows(1)
					.row()
					.column("LastStatusChange").value().isEqualTo(LocalDateTime.ofInstant(beforeChange, ZoneId.systemDefault()));
		}
	}
	
	@Test
	void updateChannelStatusTime() throws SQLException{
		try(var factory = mockStatic(TimeFactory.class)){
			var beforeChange = Instant.now().with(ChronoField.NANO_OF_SECOND, 0);
			factory.when(TimeFactory::now).thenReturn(beforeChange);
			
			tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
			assertThat(tableChannel.get()).hasNumberOfRows(1);
			
			var newTime = beforeChange.plus(1, ChronoUnit.HOURS);
			tested.updateChannelStatusTime(CHANNEL_ID, newTime);
			
			assertThat(tableChannel.get()).hasNumberOfRows(1)
					.row()
					.column("LastStatusChange").value().isEqualTo(LocalDateTime.ofInstant(newTime, ZoneId.systemDefault()));
		}
	}
}