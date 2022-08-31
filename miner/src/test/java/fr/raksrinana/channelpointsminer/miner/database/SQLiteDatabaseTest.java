package fr.raksrinana.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.SQLException;
import static org.assertj.db.api.Assertions.assertThat;
import static org.assertj.db.output.Outputs.output;

class SQLiteDatabaseTest{
	private static final String CHANNEL_ID = "channel-id";
	private static final String CHANNEL_USERNAME = "channel-username";
	
	@TempDir
	private Path tempPath;
	
	private SQLiteDatabase tested;
	private HikariDataSource dataSource;
	
	private Table tableBalance;
	private Table tableChannel;
	private Table tablePrediction;
	private Table tablePredictionUser;
	private Table tableUserPrediction;
	
	@BeforeEach
	void setUp() throws SQLException{
		var poolConfiguration = new HikariConfig();
		poolConfiguration.setJdbcUrl("jdbc:sqlite:" + tempPath.resolve(System.currentTimeMillis() + "_test.db").toAbsolutePath());
		poolConfiguration.setDriverClassName("org.sqlite.JDBC");
		poolConfiguration.setMaximumPoolSize(1);
		
		dataSource = new HikariDataSource(poolConfiguration);
		tested = new SQLiteDatabase(dataSource);
		
		tested.initDatabase();
		
		tableBalance = new Table(dataSource, "Balance");
		tableChannel = new Table(dataSource, "Channel");
		tablePrediction = new Table(dataSource, "Prediction");
		tablePredictionUser = new Table(dataSource, "PredictionUser");
		tableUserPrediction = new Table(dataSource, "UserPrediction");
	}
	
	@AfterEach
	void tearDown(){
		tested.close();
	}
	
	@Test
	void tablesAreCreated(){
		assertThat(tableBalance).exists();
		assertThat(tableChannel).exists();
		assertThat(tablePrediction).exists();
		assertThat(tablePredictionUser).exists();
		assertThat(tableUserPrediction).exists();
	}
	
	@Test
	void newChannelIsInsertedIfNew() throws SQLException{
		assertThat(tableChannel).isEmpty();
		
		tested.createChannel(CHANNEL_ID, CHANNEL_USERNAME);
		
		output(tableChannel).toConsole();
		assertThat(tableChannel)
				.hasNumberOfRows(1) // TODO Hmmm why isn't it passing, if looking manually there is indeed 1 record :/ Because of Hikari that has a pool?
				.row()
				.column("ID").value().isEqualTo(CHANNEL_ID)
				.column("Username").value().isEqualTo(CHANNEL_USERNAME)
				.column("LastStatusChange").value().isNotNull();
	}
}