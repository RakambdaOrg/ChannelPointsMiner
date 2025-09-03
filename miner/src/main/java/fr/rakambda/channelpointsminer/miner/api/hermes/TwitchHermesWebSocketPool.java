package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ITwitchHermesWebSocketResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.NotificationResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ReconnectResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.UnsubscribeResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification.PubSubNotificationType;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.ITwitchPubSubMessageListener;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.ErrorEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.factory.TwitchWebSocketClientFactory;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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
	private final IEventManager eventManager;
	
	private final Collection<TwitchHermesWebSocketClient> clients;
	private final Collection<ITwitchHermesMessageListener> listeners;
	private final Collection<ITwitchPubSubMessageListener> pubSubListeners;
	private final Queue<Topic> pendingTopics;
	
	private final Map<String, fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic> topics;
	
	public TwitchHermesWebSocketPool(int maxSubscriptionPerClient, @NonNull TwitchLogin twitchLogin, @NonNull IEventManager eventManager){
		this.maxSubscriptionPerClient = maxSubscriptionPerClient;
		this.twitchLogin = twitchLogin;
		this.eventManager = eventManager;
		
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
	
	public void removePubSubTopic(@NonNull Topic topic){
		var subscriptionId = topics.entrySet().stream().filter(e -> Objects.equals(e.getValue(), topic)).findFirst();
		if(subscriptionId.isEmpty()){
			return;
		}
		clients.stream()
				.filter(client -> client.isPubSubTopicListened(topic))
				.forEach(client -> client.removeSubscription(subscriptionId.get().getKey()));
	}
	
	public void addListener(@NonNull ITwitchHermesMessageListener listener){
		listeners.add(listener);
	}
	
	public void addPubSubListener(@NonNull ITwitchPubSubMessageListener listener){
		pubSubListeners.add(listener);
	}
	
	@Override
	public void onWebSocketMessage(@NonNull ITwitchHermesWebSocketResponse response){
		if(response instanceof UnsubscribeResponse u){
			topics.remove(u.getUnsubscribeResponse().getSubscription().getId());
		}
		if(response instanceof NotificationResponse n){
			if(n.getNotification() instanceof PubSubNotificationType t){
				if(Objects.isNull(t.getPubsub())){
					return;
				}
				var subscriptionId = n.getNotification().getSubscription().getId();
				var topic = topics.get(subscriptionId);
				if(Objects.isNull(topic)){
					log.error("Received Hermes PubSub message for unknown topic from subscription id {}", subscriptionId);
					eventManager.onEvent(new ErrorEvent("Hermes API", "Received PubSub message for unknown topic from subscription id %s".formatted(subscriptionId)));
					return;
				}
				pubSubListeners.forEach(l -> l.onTwitchMessage(topic, t.getPubsub()));
			}
		}
	}
	
	@Override
	public void onWebSocketClosed(@NonNull TwitchHermesWebSocketClient client, int code, @Nullable String reason, boolean remote){
		clients.remove(client);
		if(code != NORMAL){
			pendingTopics.addAll(client.getSubscribeRequests().keySet().stream().map(topics::get).filter(Objects::nonNull).toList());
			client.getSubscribeRequests().keySet().forEach(topics::remove);
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
			eventManager.onEvent(new ErrorEvent("Hermes API", "Failed to join pending subscriptions"));
		}
	}
	
	public void listenPubSubTopic(@NonNull Topic topic){
		if(isTopicListened(topic)){
			log.debug("Topic {} is already being listened", topics);
			return;
		}
		
		try{
			getAvailableClient().listenPubSubTopic(topic).ifPresent(subscriptionId -> topics.put(subscriptionId, topic));
		}
		catch(RuntimeException e){
			pendingTopics.add(topic);
			eventManager.onEvent(new ErrorEvent("Hermes API", "Failed to listen to PubSub topic", e));
			throw e;
		}
	}
	
	private boolean isTopicListened(@NonNull Topic topic){
		return clients.stream().anyMatch(client -> client.isPubSubTopicListened(topic));
	}
	
	@NonNull
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
	
	@NonNull
	public TwitchHermesWebSocketClient createNewClient(){
		try{
			var client = TwitchWebSocketClientFactory.createHermesClient(eventManager);
			log.info("Created Hermes WebSocket client with uuid {}", client.getUuid());
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
	
	@NonNull
	private TwitchHermesWebSocketClient createReconnectClient(@NonNull String reconnectUrl){
		try{
			var client = TwitchWebSocketClientFactory.createHermesClient(reconnectUrl, eventManager);
			log.info("Created (reconnect) Hermes WebSocket client with uuid {}", client.getUuid());
			client.addListener(this);
			client.connectBlocking();
			clients.add(client);
			return client;
		}
		catch(Exception e){
			log.error("Failed to create new (reconnect) Hermes WebSocket");
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
