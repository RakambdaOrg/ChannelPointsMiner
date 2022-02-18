package fr.raksrinana.channelpointsminer.factory;

import fr.raksrinana.channelpointsminer.config.DatabaseConfiguration;
import fr.raksrinana.channelpointsminer.database.IDatabase;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

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
		lenient().when(databaseConfiguration.getHost()).thenReturn(HOST);
		lenient().when(databaseConfiguration.getPort()).thenReturn(PORT);
		lenient().when(databaseConfiguration.getUsername()).thenReturn(USERNAME);
		lenient().when(databaseConfiguration.getPassword()).thenReturn(PASSWORD);
		lenient().when(databaseConfiguration.getDatabase()).thenReturn(DATABASE);
	}
	
	@Test
	void createDatabaseException(){
		assertThrows(Exception.class, () -> DatabaseFactory.createDatabase(databaseConfiguration));
	}
	
	@Test
	void createDatabaseHandler(){
		var database = mock(IDatabase.class);
		
		var handler = DatabaseFactory.createDatabaseHandler(database);
		assertThat(handler).isNotNull();
	}
}