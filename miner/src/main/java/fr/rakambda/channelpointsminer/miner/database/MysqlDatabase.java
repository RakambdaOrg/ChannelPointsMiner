package fr.rakambda.channelpointsminer.miner.database;

import javax.sql.DataSource;

public class MysqlDatabase extends MariaDBDatabase{
    public MysqlDatabase(DataSource dataSource){
        super(dataSource);
    }
    
    @Override
    public void initDatabase(){
        applyFlyway("db/migrations/mysql");
    }
}