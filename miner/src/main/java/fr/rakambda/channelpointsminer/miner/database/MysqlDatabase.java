package fr.rakambda.channelpointsminer.miner.database;

import com.zaxxer.hikari.HikariDataSource;

public class MysqlDatabase extends MariaDBDatabase{
    public MysqlDatabase(HikariDataSource dataSource){
        super(dataSource);
    }
    
    @Override
    public void initDatabase(){
        applyFlyway("db/migrations/mysql");
    }
}