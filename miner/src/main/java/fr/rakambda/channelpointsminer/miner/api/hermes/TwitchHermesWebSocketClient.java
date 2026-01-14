package fr.rakambda.channelpointsminer.miner.api.hermes;

import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.AuthenticateRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.ITwitchHermesWebSocketRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.SubscribeRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.UnsubscribeRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.subscribe.PubSubSubscribeType;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.AuthenticateResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ITwitchHermesWebSocketResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.KeepAliveResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.NotificationResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ReconnectResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.SubscribeResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.UnsubscribeResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.WelcomeResponse;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.event.impl.ErrorEvent;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NonNull;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.java_websocket.framing.CloseFrame.GOING_AWAY;

@Log4j2
public class TwitchHermesWebSocketClient extends WebSocketClient{
	private final Collection<ITwitchHermesWebSocketListener> listeners;
	@Getter
	private final String uuid;
	@Getter
	private final Map<String, SubscribeRequest> subscribeRequests;
	@NonNull
	private final IEventManager eventManager;
	
	@Getter
	private Instant lastPong;
	
	public TwitchHermesWebSocketClient(@NonNull URI uri, @NonNull IEventManager eventManager){
		super(uri);
		this.eventManager = eventManager;
		uuid = UUID.randomUUID().toString();
		subscribeRequests = new HashMap<>();
		
		setConnectionLostTimeout(0);
		listeners = new ConcurrentLinkedQueue<>();
		lastPong = Instant.EPOCH;
		
		addHeader("Origin", "https://www.twitch.tv");
	}
	
	@Override
	public void onOpen(ServerHandshake serverHandshake){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.info("Hermes WebSocket opened");
		}
	}
	
	@Override
	public void onMessage(String messageStr){
		try(var logContext = LogContext.empty().withSocketId(uuid)){
			log.trace("Received Hermes WebSocket message: {}", messageStr.strip());
			var message = JacksonUtils.read(messageStr, new TypeReference<ITwitchHermesWebSocketResponse>(){});
			log.trace("Parsed Hermes message: {}", message);
			
			switch(message){
				case WelcomeResponse welcomeResponse -> log.info("Received Hermes welcome with keep alive of {} seconds", welcomeResponse.getWelcome().getKeepaliveSec());
				case AuthenticateResponse authenticateResponse -> {
					if(authenticateResponse.hasError()){
						log.error("Received Hermes error authentication {}", authenticateResponse);
						close(GOING_AWAY, "Invalid credentials");
					}
				}
				case KeepAliveResponse ignored -> onPong();
				case SubscribeResponse subscribeResponse -> log.debug("Received Hermes subscribe response with status {}", subscribeResponse.getSubscribeResponse().getResult());
				case UnsubscribeResponse unsubscribeResponse -> {
					log.debug("Received Hermes unsubscribe response with status {}", unsubscribeResponse.getUnsubscribeResponse().getResult());
					subscribeRequests.remove(unsubscribeResponse.getUnsubscribeResponse().getSubscription().getId());
				}
				case NotificationResponse notificationResponse -> log.debug("Received Hermes notification of type {}", notificationResponse.getNotification().getClass().getSimpleName());
				case ReconnectResponse ignored -> {
					log.warn("Received Hermes reconnect response");
					close(GOING_AWAY);
				}
				default -> {
				}
			}
			listeners.forEach(listener -> listener.onWebSocketMessage(message));
		}
		catch(Exception e){
			log.error("Failed to handle Hermes WebSocket message {}", messageStr, e);
			eventManager.onEvent(new ErrorEvent("Hermes API", "Failed to handle Hermes WebSocket message %s".formatted(messageStr), e));
		}
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.info("Hermes WebSocket closed with code {}, from host {}, reason {}", code, remote, reason);
			listeners.forEach(l -> l.onWebSocketClosed(this, code, reason, remote));
		}
	}
	
	@Override
	public void onError(Exception e){
		log.error("Error from Hermes WebSocket", e);
	}
	
	private void onPong(){
		lastPong = TimeFactory.now();
	}
	
	public void authenticate(@NonNull TwitchLogin twitchLogin){
		send(new AuthenticateRequest(twitchLogin.getAccessToken()));
	}
	
	public void send(@NonNull ITwitchHermesWebSocketRequest request){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			var data = JacksonUtils.writeAsString(request);
			log.trace("Sending Hermes WebSocket message: {}", data);
			send(data);
		}
		catch(JacksonException e){
			log.error("Failed to convert Hermes WebSocket message to json", e);
		}
	}
	
	@Override
	public void onWebsocketPong(WebSocket conn, Framedata f){
		onPong();
	}
	
	public void addListener(@NonNull ITwitchHermesWebSocketListener listener){
		listeners.add(listener);
	}
	
	public boolean isPubSubTopicListened(@NonNull Topic topic){
		return subscribeRequests.values().stream()
				.map(SubscribeRequest::getSubscribe)
				.filter(PubSubSubscribeType.class::isInstance)
				.map(PubSubSubscribeType.class::cast)
				.anyMatch(t -> Objects.equals(t.getPubsub().getTopic(), topic.getValue()));
	}
	
	public Optional<String> listenPubSubTopic(@NonNull Topic topic){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			var request = SubscribeRequest.pubsub(topic.getValue());
			subscribeRequests.put(request.getSubscribe().getId(), request);
			send(request);
			return Optional.of(request.getSubscribe().getId());
		}
	}
	
	public void removeSubscription(@NonNull String id){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			if(subscribeRequests.containsKey(id)){
				send(new UnsubscribeRequest(id));
			}
		}
	}
	
	public int getSubscriptionCount(){
		return subscribeRequests.size();
	}
}
