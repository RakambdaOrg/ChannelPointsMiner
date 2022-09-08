package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.config.DatabaseConfiguration;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.database.NoOpDatabase;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class DatabaseFactoryTest{
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String HOST = "host";
	private static final int PORT = 1234;
	private static final String DATABASE = "database";
	
	@Mock
	private DatabaseConfiguration databaseConfiguration;
	
	@BeforeEach
	void setUp(){
		lenient().when(databaseConfiguration.getJdbcUrl()).thenReturn("jdbc:mariadb://%s:%d/%s".formatted(HOST, PORT, DATABASE));
		lenient().when(databaseConfiguration.getUsername()).thenReturn(USERNAME);
		lenient().when(databaseConfiguration.getPassword()).thenReturn(PASSWORD);
	}
	
	@Test
	void createNoOpDatabase() throws SQLException{
		assertThat(DatabaseFactory.createDatabase(null)).isInstanceOf(NoOpDatabase.class);
	}
	
	@Test
	void createDatabaseException(){
		assertThrows(Exception.class, () -> DatabaseFactory.createDatabase(databaseConfiguration));
	}
	
	@Test
	void createDatabaseInvalidJdbcUrl(){
		when(databaseConfiguration.getJdbcUrl()).thenReturn("jdbc:mariadb");
		
		assertThrows(IllegalStateException.class, () -> DatabaseFactory.createDatabase(databaseConfiguration));
	}
	
	@Test
	void createDatabaseInvalidJdbcType(){
		when(databaseConfiguration.getJdbcUrl()).thenReturn("jdbc:unknown");
		
		assertThrows(IllegalStateException.class, () -> DatabaseFactory.createDatabase(databaseConfiguration));
	}
	
	@Test
	void createDatabaseHandler(){
		var database = mock(IDatabase.class);
		
		var handler = DatabaseFactory.createDatabaseHandler(database);
		assertThat(handler).isNotNull();
	}
}