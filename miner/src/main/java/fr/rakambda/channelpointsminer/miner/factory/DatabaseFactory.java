package fr.rakambda.channelpointsminer.miner.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import fr.rakambda.channelpointsminer.miner.config.DatabaseConfiguration;
import fr.rakambda.channelpointsminer.miner.database.DatabaseEventHandler;
import fr.rakambda.channelpointsminer.miner.database.IDatabase;
import fr.rakambda.channelpointsminer.miner.database.MariaDBDatabase;
import fr.rakambda.channelpointsminer.miner.database.MysqlDatabase;
import fr.rakambda.channelpointsminer.miner.database.NoOpDatabase;
import fr.rakambda.channelpointsminer.miner.database.PostgreSqlDatabase;
import fr.rakambda.channelpointsminer.miner.database.SQLiteDatabase;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseFactory{
	
	@NonNull
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
			case "mariadb" -> createMariaDbDatabase(configuration);
			case "sqlite" -> createSqliteDatabase(configuration);
			case "mysql" -> createMysqlDatabase(configuration);
			case "postgresql" -> createPostgreSqlDatabase(configuration);
			default -> throw new IllegalStateException("Unknown JDBC type " + parts[1]);
		};
		
		database.initDatabase();
		return database;
	}
	
	@NonNull
	private static HikariConfig createHikariConfiguration(@NonNull DatabaseConfiguration configuration, String driver){
		var config = new HikariConfig();
		config.setJdbcUrl(configuration.getJdbcUrl());
		config.setUsername(configuration.getUsername());
		config.setPassword(configuration.getPassword());
		config.setDriverClassName(driver);
		config.setConnectionTimeout(configuration.getConnectionTimeout());
		config.setIdleTimeout(configuration.getIdleTimeout());
		config.setMaxLifetime(configuration.getLifetimeTimeout());
		config.setMaximumPoolSize(config.getMaximumPoolSize());
		return config;
	}
	
	@NonNull
	private static MariaDBDatabase createMariaDbDatabase(@NonNull DatabaseConfiguration configuration){
		var config = createHikariConfiguration(configuration, "org.mariadb.jdbc.Driver");
		return new MariaDBDatabase(new HikariDataSource(config));
	}
	
	@NonNull
	private static MariaDBDatabase createMysqlDatabase(@NonNull DatabaseConfiguration configuration){
		var config = createHikariConfiguration(configuration, "com.mysql.cj.jdbc.Driver");
		return new MysqlDatabase(new HikariDataSource(config));
	}
	
	@NonNull
	private static SQLiteDatabase createSqliteDatabase(@NonNull DatabaseConfiguration configuration){
		var config = createHikariConfiguration(configuration, "org.sqlite.JDBC");
		config.setMaximumPoolSize(1);
		return new SQLiteDatabase(new HikariDataSource(config));
	}
	
	@NonNull
	private static PostgreSqlDatabase createPostgreSqlDatabase(
			@NonNull DatabaseConfiguration configuration){
		var config = createHikariConfiguration(configuration, "org.postgresql.Driver");
		return new PostgreSqlDatabase(new HikariDataSource(config));
	}
	
	@NonNull
	public static DatabaseEventHandler createDatabaseHandler(@NonNull IDatabase database, boolean recordUserPredictions){
		return new DatabaseEventHandler(database, recordUserPredictions);
	}
}
