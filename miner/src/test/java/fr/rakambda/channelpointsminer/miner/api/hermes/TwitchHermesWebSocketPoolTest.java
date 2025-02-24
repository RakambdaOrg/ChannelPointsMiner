package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.AuthenticateRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.topic.TopicName;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.topic.Topics;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import java.time.temporal.ValueRange;
import static org.junit.jupiter.api.Assertions.*;

class TwitchHermesWebSocketPoolTest{
	@SneakyThrows
	@Test
	void name(){
		var pool = new TwitchHermesWebSocketPool(100);
		var client = pool.createNewClient();
		
		while(!client.isOpen()){
			Thread.sleep(100);
		}
		
		client.send(new AuthenticateRequest(""));
		
		while(true){
			Thread.sleep(100);	
		}
	}
}