package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Log4j2
public class TwitchWebSocketPool implements AutoCloseable{
	private static final int MAX_TOPIC_PER_CLIENT = 50;
	
	private final Collection<TwitchWebSocketClient> clients;
	
	public TwitchWebSocketPool(){
		clients = new ArrayList<>();
	}
	
	public void ping(){
		clients.stream()
				.filter(WebSocketClient::isOpen)
				.filter(client -> !client.isClosing())
				.forEach(TwitchWebSocketClient::ping);
	}
	
	public void listenTopic(@NotNull TopicName name, @NotNull String target){
		listenTopic(Topics.buildFromName(name, target, Main.getTwitchLogin().getAccessToken()));
	}
	
	public void listenTopic(@NotNull Topics topics){
		var isListened = topics.getTopics().stream().anyMatch(t -> clients.stream().anyMatch(c -> c.isTopicListened(t)));
		if(isListened){
			log.debug("Topic {} is already being listened", topics);
			return;
		}
		getAvailableClient().listenTopic(topics);
	}
	
	@NotNull
	private TwitchWebSocketClient getAvailableClient(){
		return clients.stream()
				.filter(client -> client.getTopicCount() < MAX_TOPIC_PER_CLIENT)
				.findAny()
				.orElseGet(this::createNewClient);
	}
	
	@NotNull
	private TwitchWebSocketClient createNewClient(){
		try{
			var client = new TwitchWebSocketClient();
			client.connectBlocking();
			clients.add(client);
			return client;
		}
		catch(Exception e){
			log.error("Failed to create new websocket");
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close(){
		clients.forEach(WebSocketClient::close);
	}
}
