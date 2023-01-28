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
import fr.rakambda.channelpointsminer.miner.database.SQLiteDatabase;
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
            case "mariadb" -> createMariaDbDatabase(configuration);
            case "sqlite" -> createSqliteDatabase(configuration);
            case "mysql" -> createMysqlDatabase(configuration);
            default -> throw new IllegalStateException("Unknown JDBC type " + parts[1]);
        };
        
        database.initDatabase();
        return database;
    }
    
    @NotNull
    private static HikariConfig createHikariConfiguration(@NotNull DatabaseConfiguration configuration, String driver){
        var config = new HikariConfig();
        config.setJdbcUrl(configuration.getJdbcUrl());
        config.setUsername(configuration.getUsername());
        config.setPassword(configuration.getPassword());
        config.setDriverClassName(driver);
        config.setConnectionTimeout(configuration.getConnectionTimeout());
        config.setIdleTimeout(configuration.getIdleTimeout());
        config.setMaxLifetime(configuration.getLifetimeTimeout());
        return config;
    }
    
    @NotNull
    private static MariaDBDatabase createMariaDbDatabase(@NotNull DatabaseConfiguration configuration){
        var config = createHikariConfiguration(configuration, "org.mariadb.jdbc.Driver");
        config.setMaximumPoolSize(configuration.getMaxPoolSize());
        return new MariaDBDatabase(new HikariDataSource(config));
    }
    
    @NotNull
    private static MariaDBDatabase createMysqlDatabase(@NotNull DatabaseConfiguration configuration){
        var config = createHikariConfiguration(configuration, "com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(configuration.getMaxPoolSize());
        return new MysqlDatabase(new HikariDataSource(config));
    }
    
    @NotNull
    private static SQLiteDatabase createSqliteDatabase(@NotNull DatabaseConfiguration configuration){
        var config = createHikariConfiguration(configuration, "org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        return new SQLiteDatabase(new HikariDataSource(config));
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
