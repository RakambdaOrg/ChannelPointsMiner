package fr.rakambda.channelpointsminer.miner.api.hermes;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ITwitchHermesWebSocketResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.NotificationResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.UnsubscribeResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.PubSubNotificationType;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.ITwitchPubSubMessageListener;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.message.IPubSubMessage;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.factory.TwitchWebSocketClientFactory;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;

@Log4j2
public class TwitchHermesWebSocketPool implements AutoCloseable, ITwitchHermesWebSocketListener{
	private static final int SOCKET_TIMEOUT_MINUTES = 5;
	
	private final int maxSubscriptionPerClient;
	private final TwitchLogin twitchLogin;
	
	private final Collection<TwitchHermesWebSocketClient> clients;
	private final Collection<ITwitchHermesMessageListener> listeners;
	private final Collection<ITwitchPubSubMessageListener> pubSubListeners;
	private final Queue<Topic> pendingTopics;
	
	private final Map<String, fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic> topics;
	
	public TwitchHermesWebSocketPool(int maxSubscriptionPerClient, @NotNull TwitchLogin twitchLogin){
		this.maxSubscriptionPerClient = maxSubscriptionPerClient;
		this.twitchLogin = twitchLogin;
		
		clients = new ConcurrentLinkedQueue<>();
		listeners = new ConcurrentLinkedQueue<>();
		pubSubListeners = new ConcurrentLinkedQueue<>();
		pendingTopics = new ConcurrentLinkedQueue<>();
		topics = new ConcurrentHashMap<>();
	}
	
	public void ping(){
		checkStaleConnection();
	}
	
	public void checkStaleConnection(){
		clients.stream()
				.filter(client -> TimeFactory.now().isAfter(client.getLastPong().plus(SOCKET_TIMEOUT_MINUTES, MINUTES)))
				.forEach(client -> client.close(ABNORMAL_CLOSE, "Timeout reached"));
	}
	
	public void removePubSubTopic(@NotNull Topic topic){
		var subscriptionId = topics.entrySet().stream().filter(e -> Objects.equals(e.getValue(), topic)).findFirst();
		if(subscriptionId.isEmpty()){
			return;
		}
		clients.stream()
				.filter(client -> client.isPubSubTopicListened(topic))
				.forEach(client -> client.removeSubscription(subscriptionId.get().getKey()));
	}
	
	public void addListener(@NotNull ITwitchHermesMessageListener listener){
		listeners.add(listener);
	}
	
	public void addPubSubListener(@NotNull ITwitchPubSubMessageListener listener){
		pubSubListeners.add(listener);
	}
	
	@Override
	public void onWebSocketMessage(@NotNull ITwitchHermesWebSocketResponse response){
		if(response instanceof UnsubscribeResponse u){
			topics.remove(u.getUnsubscribeResponse().getSubscription().getId());
		}
		if(response instanceof NotificationResponse n){
			if(n.getNotification() instanceof PubSubNotificationType t){
				try{
					var topic = topics.get(n.getNotification().getSubscription().getId());
					var message = JacksonUtils.read(t.getPubsub(), new TypeReference<IPubSubMessage>(){});
					pubSubListeners.forEach(l -> l.onTwitchMessage(topic, message));
				}
				catch(IOException e){
					log.error("Failed to parse PubSub notification from Hermes {}", t.getPubsub(), e);
				}
			}
		}
	}
	
	@Override
	public void onWebSocketClosed(@NotNull TwitchHermesWebSocketClient client, int code, @Nullable String reason, boolean remote){
		clients.remove(client);
		if(code != NORMAL){
			pendingTopics.addAll(client.getSubscribeRequests().keySet().stream().map(topics::get).filter(Objects::nonNull).toList());
		}
	}
	
	public void listenPendingPubSubTopics(){
		try{
			Topic topic;
			while(Objects.nonNull(topic = pendingTopics.poll())){
				listenPubSubTopic(topic);
			}
		}
		catch(RuntimeException e){
			log.error("Failed to join pending subscriptions", e);
		}
	}
	
	public void listenPubSubTopic(@NotNull Topic topic){
		if(isTopicListened(topic)){
			log.debug("Topic {} is already being listened", topics);
			return;
		}
		
		try{
			getAvailableClient().listenPubSubTopic(topic).ifPresent(subscriptionId -> topics.put(subscriptionId, topic));
		}
		catch(RuntimeException e){
			pendingTopics.add(topic);
			throw e;
		}
	}
	
	private boolean isTopicListened(@NotNull Topic topic){
		return clients.stream().anyMatch(client -> client.isPubSubTopicListened(topic));
	}
	
	@NotNull
	private TwitchHermesWebSocketClient getAvailableClient(){
		return clients.stream()
				.filter(client -> !client.isClosing() && !client.isClosed())
				.filter(client -> client.getSubscriptionCount() < maxSubscriptionPerClient)
				.findAny()
				.orElseGet(() -> {
					var client = createNewClient();
					client.authenticate(twitchLogin);
					return client;
				});
	}
	
	@NotNull
	public TwitchHermesWebSocketClient createNewClient(){
		try{
			var client = TwitchWebSocketClientFactory.createHermesClient();
			log.debug("Created Hermes WebSocket client with uuid {}", client.getUuid());
			client.addListener(this);
			client.connectBlocking();
			clients.add(client);
			return client;
		}
		catch(Exception e){
			log.error("Failed to create new Hermes WebSocket");
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
