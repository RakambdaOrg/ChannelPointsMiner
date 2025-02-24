package fr.rakambda.channelpointsminer.miner.api.hermes;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.java_websocket.framing.CloseFrame.GOING_AWAY;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.ITwitchHermesWebSocketRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.ListenTopicRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.KeepAliveRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.UnlistenTopicRequest;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.request.topic.Topics;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.AuthenticateResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ITwitchHermesWebSocketResponse;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.MessageResponseHermes;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.PongResponseHermes;
import fr.rakambda.channelpointsminer.miner.api.hermes.data.response.ReconnectResponseHermes;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class TwitchHermesWebSocketClient extends WebSocketClient{
	@Getter
	private final Set<Topics> topics;
	private final Collection<ITwitchHermesWebSocketListener> listeners;
	@Getter
	private final String uuid;
	private final Map<String, ListenTopicRequest> listenRequests;
	
	@Getter
	private Instant lastPong;
	
	public TwitchHermesWebSocketClient(@NotNull URI uri){
		super(uri);
		uuid = UUID.randomUUID().toString();
		listenRequests = new HashMap<>();
		
		setConnectionLostTimeout(0);
		topics = new HashSet<>();
		listeners = new ConcurrentLinkedQueue<>();
		lastPong = Instant.EPOCH;
		
		addHeader("Origin", "https://www.twitch.tv");
		addHeader("Sec-Websocket-Key", "g5vRgkpsUreEDo2HQn0RgQ==");
		addHeader("Sec-Websocket-Version", "13");
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
				case AuthenticateResponse authenticateResponse -> {
					if(authenticateResponse.hasError()){
						log.error("Received Hermes error authentication {}", authenticateResponse);
						close(GOING_AWAY, "Invalid credentials");
					}
				}
				case PongResponseHermes ignored1 -> onPong();
				case MessageResponseHermes messageResponse -> {
				}
				case ReconnectResponseHermes ignored -> close(GOING_AWAY);
				default -> {
				}
			}
			listeners.forEach(listener -> listener.onWebSocketMessage(message));
		}
		catch(Exception e){
			log.error("Failed to handle Hermes WebSocket message {}", messageStr, e);
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
	
	public void ping(){
		send(new KeepAliveRequest());
	}
	
	public void send(@NotNull ITwitchHermesWebSocketRequest request){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			var data = JacksonUtils.writeAsString(request);
			log.trace("Sending Hermes WebSocket message: {}", data);
			send(data);
		}
		catch(JsonProcessingException e){
			log.error("Failed to convert Hermes WebSocket message to json", e);
		}
	}
	
	@Override
	public void onWebsocketPong(WebSocket conn, Framedata f){
		onPong();
	}
	
	public void addListener(@NotNull ITwitchHermesWebSocketListener listener){
		listeners.add(listener);
	}
	
	public boolean isTopicListened(@NotNull Topic topic){
		return topics.stream()
				.flatMap(t -> t.getTopics().stream())
				.anyMatch(t -> Objects.equals(t, topic));
	}
	
	public void listenTopic(@NotNull Topics topics){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			if(this.topics.add(topics)){
				var request = new ListenTopicRequest(topics);
				listenRequests.put(request.getNonce(), request);
				send(request);
			}
		}
	}
	
	public void removeTopic(@NotNull Topic topic){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			var topics = this.topics.stream()
					.filter(t -> t.getTopics().contains(topic))
					.toList();
			
			topics.forEach(t -> {
				send(new UnlistenTopicRequest(t));
				this.topics.remove(t);
			});
		}
	}
	
	public int getTopicCount(){
		return topics.stream().mapToInt(Topics::getTopicCount).sum();
	}
}
