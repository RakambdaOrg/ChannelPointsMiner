package fr.rakambda.channelpointsminer.viewer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class HibernateConfig implements HibernatePropertiesCustomizer{
	
	private final DataSource dataSource;
	
	@Override
	public void customize(Map<String, Object> hibernateProperties){
		try(Connection conn = dataSource.getConnection()){
			String dbName = conn.getMetaData().getDatabaseProductName().toLowerCase();
			if(dbName.contains("postgresql")){
				hibernateProperties.put("hibernate.globally_quoted_identifiers", true);
				log.info("Set property hibernate.globally_quoted_identifiers to true");
			}
		}
		catch(Exception e){
			log.error("Could not determine database type", e);
			log.warn("Skipping automatic database configuration.");
		}
	}
}
