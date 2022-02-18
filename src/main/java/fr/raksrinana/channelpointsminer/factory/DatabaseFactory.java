package fr.raksrinana.channelpointsminer.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import fr.raksrinana.channelpointsminer.config.DatabaseConfiguration;
import fr.raksrinana.channelpointsminer.database.DatabaseHandler;
import fr.raksrinana.channelpointsminer.database.IDatabase;
import fr.raksrinana.channelpointsminer.database.MariaDBDatabase;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;

public class DatabaseFactory{
	public static IDatabase createDatabase(@NotNull DatabaseConfiguration configuration) throws SQLException, HikariPool.PoolInitializationException{
		var dbURL = "jdbc:mariadb://%s:%d/%s".formatted(
				configuration.getHost(),
				configuration.getPort(),
				configuration.getDatabase()
		);
		
		var poolConfiguration = new HikariConfig();
		poolConfiguration.setJdbcUrl(dbURL);
		poolConfiguration.setUsername(configuration.getUsername());
		poolConfiguration.setPassword(configuration.getPassword());
		poolConfiguration.setDriverClassName("org.mariadb.jdbc.Driver");
		
		var db = new MariaDBDatabase(new HikariDataSource(poolConfiguration));
		db.initDatabase();
		return db;
	}
	
	public static DatabaseHandler createDatabaseHandler(@NotNull IDatabase database){
		return new DatabaseHandler(database);
	}
}
