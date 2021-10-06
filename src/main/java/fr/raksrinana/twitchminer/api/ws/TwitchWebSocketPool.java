package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.api.ws.data.response.TwitchWebSocketResponse;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;

@Log4j2
public class TwitchWebSocketPool implements AutoCloseable, TwitchWebSocketListener{
	private static final int MAX_TOPIC_PER_CLIENT = 50;
	private static final int SOCKET_TIMEOUT_MINUTES = 5;
	
	private final Collection<TwitchWebSocketClient> clients;
	private final List<TwitchWebSocketListener> listeners;
	
	public TwitchWebSocketPool(){
		clients = new ArrayList<>();
		listeners = new ArrayList<>();
	}
	
	public void ping(){
		clients.stream()
				.filter(client -> Instant.now().isAfter(Instant.ofEpochMilli(client.getLastPong()).plus(SOCKET_TIMEOUT_MINUTES, MINUTES)))
				.forEach(client -> client.close(ABNORMAL_CLOSE, "Timeout reached"));
		
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
			client.addListener(this);
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
	public void onWebSocketMessage(@NotNull TwitchWebSocketResponse message){
		listeners.forEach(l -> l.onWebSocketMessage(message));
	}
	
	@Override
	public void onWebSocketClosed(@NotNull TwitchWebSocketClient client, int code, @Nullable String reason, boolean remote){
		clients.remove(client);
		var allTopics = client.getTopics().stream().collect(new Topics.TopicsCollector());
		listenTopic(allTopics);
	}
	
	@Override
	public void close(){
		clients.forEach(WebSocketClient::close);
	}
}
