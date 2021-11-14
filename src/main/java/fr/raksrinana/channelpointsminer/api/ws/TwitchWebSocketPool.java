package fr.raksrinana.channelpointsminer.api.ws;

import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.api.ws.data.response.ITwitchWebSocketResponse;
import fr.raksrinana.channelpointsminer.api.ws.data.response.MessageResponse;
import fr.raksrinana.channelpointsminer.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.factory.TwitchWebSocketClientFactory;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;

@Log4j2
public class TwitchWebSocketPool implements AutoCloseable, ITwitchWebSocketListener{
	private static final int SOCKET_TIMEOUT_MINUTES = 5;
	
	private final Collection<TwitchWebSocketClient> clients;
	private final List<ITwitchMessageListener> listeners;
	private final int maxTopicPerClient;
	
	public TwitchWebSocketPool(int maxTopicPerClient){
		this.maxTopicPerClient = maxTopicPerClient;
		clients = new ArrayList<>();
		listeners = new ArrayList<>();
	}
	
	public void ping(){
		clients.stream()
				.filter(client -> TimeFactory.now().isAfter(client.getLastPong().plus(SOCKET_TIMEOUT_MINUTES, MINUTES)))
				.forEach(client -> client.close(ABNORMAL_CLOSE, "Timeout reached"));
		
		clients.stream()
				.filter(WebSocketClient::isOpen)
				.filter(client -> !client.isClosing())
				.forEach(TwitchWebSocketClient::ping);
	}
	
	public void listenTopic(@NotNull Topics topics){
		var isListened = topics.getTopics().stream().anyMatch(this::isTopicListened);
		if(isListened){
			log.debug("Topic {} is already being listened", topics);
			return;
		}
		getAvailableClient().listenTopic(topics);
	}
	
	private boolean isTopicListened(@NotNull Topic topic){
		return clients.stream().anyMatch(client -> client.isTopicListened(topic));
	}
	
	public void removeTopic(@NotNull Topic topic){
		clients.stream()
				.filter(client -> client.isTopicListened(topic))
				.forEach(client -> client.removeTopic(topic));
	}
	
	@Override
	public void onWebSocketClosed(@NotNull TwitchWebSocketClient client, int code, @Nullable String reason, boolean remote){
		clients.remove(client);
		if(code != NORMAL){
			client.getTopics().forEach(this::listenTopic);
		}
	}
	
	@NotNull
	private TwitchWebSocketClient createNewClient(){
		try{
			var client = TwitchWebSocketClientFactory.createClient();
			log.debug("Created websocket client with uuid {}", client.getUuid());
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
	
	public void addListener(@NotNull ITwitchMessageListener listener){
		listeners.add(listener);
	}
	
	@Override
	public void onWebSocketMessage(@NotNull ITwitchWebSocketResponse response){
		if(response instanceof MessageResponse m){
			var topic = m.getData().getTopic();
			var message = m.getData().getMessage();
			listeners.forEach(l -> l.onTwitchMessage(topic, message));
		}
	}
	
	@NotNull
	private TwitchWebSocketClient getAvailableClient(){
		return clients.stream()
				.filter(client -> client.getTopicCount() < maxTopicPerClient)
				.findAny()
				.orElseGet(this::createNewClient);
	}
	
	public int getClientCount(){
		return clients.size();
	}
	
	@Override
	public void close(){
		clients.forEach(WebSocketClient::close);
	}
}
