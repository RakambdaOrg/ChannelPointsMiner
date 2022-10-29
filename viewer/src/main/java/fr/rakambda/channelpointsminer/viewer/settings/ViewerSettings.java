package fr.rakambda.channelpointsminer.viewer.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "channel-points-miner.viewer")
public class ViewerSettings{

}
