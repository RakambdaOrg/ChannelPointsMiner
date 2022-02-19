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
	@NotNull
	public static IDatabase createDatabase(@NotNull DatabaseConfiguration configuration) throws SQLException, HikariPool.PoolInitializationException{
		var db = new MariaDBDatabase(createDatasource(configuration));
		db.initDatabase();
		return db;
	}
	
	@NotNull
	private static HikariDataSource createDatasource(@NotNull DatabaseConfiguration configuration){
		var jdbcUrl = configuration.getJdbcUrl();
		
		var parts = jdbcUrl.split(":");
		if(parts.length < 3){
			throw new IllegalStateException("Malformed JDBC URL");
		}
		
		var driver = switch(parts[1]){
			case "mariadb" -> "org.mariadb.jdbc.Driver";
			default -> throw new IllegalStateException("Unknown JDBC type " + parts[1]);
		};
		
		var poolConfiguration = new HikariConfig();
		poolConfiguration.setJdbcUrl(jdbcUrl);
		poolConfiguration.setUsername(configuration.getUsername());
		poolConfiguration.setPassword(configuration.getPassword());
		poolConfiguration.setDriverClassName(driver);
		
		return new HikariDataSource(poolConfiguration);
	}
	
	@NotNull
	public static DatabaseHandler createDatabaseHandler(@NotNull IDatabase database){
		return new DatabaseHandler(database);
	}
}
