package fr.raksrinana.channelpointsminer.miner.api.ws;

import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic.Topics;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.ITwitchWebSocketResponse;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.response.MessageResponse;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TwitchWebSocketClientFactory;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;

@Log4j2
public class TwitchPubSubWebSocketPool implements AutoCloseable, ITwitchPubSubWebSocketListener{
	private static final int SOCKET_TIMEOUT_MINUTES = 5;
	
	private final Collection<TwitchPubSubWebSocketClient> clients;
	private final Collection<ITwitchPubSubMessageListener> listeners;
	private final Queue<Topics> pendingTopics;
	private final int maxTopicPerClient;
	
	public TwitchPubSubWebSocketPool(int maxTopicPerClient){
		this.maxTopicPerClient = maxTopicPerClient;
		clients = new ConcurrentLinkedQueue<>();
		listeners = new ConcurrentLinkedQueue<>();
		pendingTopics = new ConcurrentLinkedQueue<>();
	}
	
	public void ping(){
		checkStaleConnection();
		
		clients.stream()
				.filter(WebSocketClient::isOpen)
				.filter(client -> !client.isClosing())
				.forEach(TwitchPubSubWebSocketClient::ping);
	}
	
	public void checkStaleConnection(){
		clients.stream()
				.filter(client -> TimeFactory.now().isAfter(client.getLastPong().plus(SOCKET_TIMEOUT_MINUTES, MINUTES)))
				.forEach(client -> client.close(ABNORMAL_CLOSE, "Timeout reached"));
	}
	
	public void removeTopic(@NotNull Topic topic){
		clients.stream()
				.filter(client -> client.isTopicListened(topic))
				.forEach(client -> client.removeTopic(topic));
	}
	
	public void addListener(@NotNull ITwitchPubSubMessageListener listener){
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
	
	@Override
	public void onWebSocketClosed(@NotNull TwitchPubSubWebSocketClient client, int code, @Nullable String reason, boolean remote){
		clients.remove(client);
		if(code != NORMAL){
			pendingTopics.addAll(client.getTopics());
		}
	}
	
	public void listenPendingTopics(){
		try{
			Topics topic;
			while(Objects.nonNull(topic = pendingTopics.poll())){
				listenTopic(topic);
			}
		}
		catch(RuntimeException e){
			log.error("Failed to join pending chats", e);
		}
	}
	
	public void listenTopic(@NotNull Topics topics){
		var isListened = topics.getTopics().stream().anyMatch(this::isTopicListened);
		if(isListened){
			log.debug("Topic {} is already being listened", topics);
			return;
		}
		
		try{
			getAvailableClient().listenTopic(topics);
		}
		catch(RuntimeException e){
			pendingTopics.add(topics);
			throw e;
		}
	}
	
	private boolean isTopicListened(@NotNull Topic topic){
		return clients.stream().anyMatch(client -> client.isTopicListened(topic));
	}
	
	@NotNull
	private TwitchPubSubWebSocketClient getAvailableClient(){
		return clients.stream()
				.filter(client -> !client.isClosing() && !client.isClosed())
				.filter(client -> client.getTopicCount() < maxTopicPerClient)
				.findAny()
				.orElseGet(this::createNewClient);
	}
	
	@NotNull
	private TwitchPubSubWebSocketClient createNewClient(){
		try{
			var client = TwitchWebSocketClientFactory.createPubSubClient();
			log.debug("Created pubsub websocket client with uuid {}", client.getUuid());
			client.addListener(this);
			client.connectBlocking();
			clients.add(client);
			return client;
		}
		catch(Exception e){
			log.error("Failed to create new pubsub websocket");
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close(){
		clients.forEach(WebSocketClient::close);
	}
	
	public int getClientCount(){
		return clients.size();
	}
}
