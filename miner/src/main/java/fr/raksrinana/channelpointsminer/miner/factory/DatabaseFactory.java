package fr.raksrinana.channelpointsminer.miner.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import fr.raksrinana.channelpointsminer.miner.config.DatabaseConfiguration;
import fr.raksrinana.channelpointsminer.miner.database.DatabaseEventHandler;
import fr.raksrinana.channelpointsminer.miner.database.IDatabase;
import fr.raksrinana.channelpointsminer.miner.database.MariaDBDatabase;
import fr.raksrinana.channelpointsminer.miner.database.NoOpDatabase;
import fr.raksrinana.channelpointsminer.miner.database.SQLiteDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseFactory{
	
	@NotNull
	public static IDatabase createDatabase(@Nullable DatabaseConfiguration configuration) throws SQLException, HikariPool.PoolInitializationException{
		if(Objects.isNull(configuration)){
			return new NoOpDatabase();
		}
		
		var jdbcUrl = configuration.getJdbcUrl();
		
		var parts = jdbcUrl.split(":");
		if(parts.length < 3){
			throw new IllegalStateException("Malformed JDBC URL");
		}
		
		var database = switch(parts[1]){
			case "mariadb" -> new MariaDBDatabase(createDatasource(configuration, "org.mariadb.jdbc.Driver", configuration.getMaxPoolSize()));
			case "sqlite" -> new SQLiteDatabase(createDatasource(configuration, "org.sqlite.JDBC", 1));
			default -> throw new IllegalStateException("Unknown JDBC type " + parts[1]);
		};
		
		database.initDatabase();
		return database;
	}
	
	@NotNull
	private static HikariDataSource createDatasource(@NotNull DatabaseConfiguration configuration, @NotNull String driver, int maxPoolSize){
		var poolConfiguration = new HikariConfig();
		poolConfiguration.setJdbcUrl(configuration.getJdbcUrl());
		poolConfiguration.setUsername(configuration.getUsername());
		poolConfiguration.setPassword(configuration.getPassword());
		poolConfiguration.setDriverClassName(driver);
		poolConfiguration.setMaximumPoolSize(maxPoolSize);
		
		return new HikariDataSource(poolConfiguration);
	}
	
	@NotNull
	public static DatabaseEventHandler createDatabaseHandler(@NotNull IDatabase database){
		return new DatabaseEventHandler(database);
	}
}
