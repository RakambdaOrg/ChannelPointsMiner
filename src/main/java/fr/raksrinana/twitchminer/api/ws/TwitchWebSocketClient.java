package fr.raksrinana.twitchminer.api.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.twitchminer.api.ws.data.request.ListenTopicRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.PingRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.TwitchWebSocketRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.api.ws.data.response.MessageResponse;
import fr.raksrinana.twitchminer.api.ws.data.response.PongResponse;
import fr.raksrinana.twitchminer.api.ws.data.response.ResponseResponse;
import fr.raksrinana.twitchminer.api.ws.data.response.TwitchWebSocketResponse;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.log.LogContext;
import fr.raksrinana.twitchminer.util.json.JacksonUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import static org.java_websocket.framing.CloseFrame.GOING_AWAY;

@Log4j2
public class TwitchWebSocketClient extends WebSocketClient{
	@Getter
	private final Set<Topics> topics;
	private final List<TwitchWebSocketListener> listeners;
	@Getter
	private final String uuid;
	
	@Getter
	private Instant lastPong;
	
	public TwitchWebSocketClient(@NotNull URI uri){
		super(uri);
		uuid = UUID.randomUUID().toString();
		
		setConnectionLostTimeout(0);
		topics = new HashSet<>();
		listeners = new ArrayList<>();
		lastPong = Instant.EPOCH;
	}
	
	@Override
	public void onOpen(ServerHandshake serverHandshake){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.info("WebSocket opened");
			ping();
		}
	}
	
	@Override
	public void onMessage(String messageStr){
		try(var logContext = LogContext.empty().withSocketId(uuid)){
			log.trace("Received Websocket message: {}", messageStr.strip());
			var message = JacksonUtils.read(messageStr, new TypeReference<TwitchWebSocketResponse>(){});
			log.trace("Parsed message: {}", message);
			
			if(message instanceof ResponseResponse responseMessage){
				if(responseMessage.hasError()){
					log.error("Received error response {}", responseMessage);
					if(Objects.equals("ERR_BADAUTH", responseMessage.getError())){
						close(GOING_AWAY, "Invalid credentials");
					}
				}
			}
			else if(message instanceof PongResponse){
				onPong();
			}
			else if(message instanceof MessageResponse messageResponse){
				logContext.withTopic(messageResponse.getData().getTopic());
			}
			listeners.forEach(listener -> listener.onWebSocketMessage(message));
		}
		catch(Exception e){
			log.error("Failed to handle WebSocket message", e);
		}
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.info("WebSocket closed with code {}, from host {}, reason {}", code, remote, reason);
			listeners.forEach(l -> l.onWebSocketClosed(this, code, reason, remote));
		}
	}
	
	@Override
	public void onError(Exception e){
		log.error("Error from WebSocket", e);
	}
	
	@Override
	public void onWebsocketPong(WebSocket conn, Framedata f){
		onPong();
	}
	
	private void onPong(){
		lastPong = TimeFactory.now();
	}
	
	public void ping(){
		send(new PingRequest());
	}
	
	public void listenTopic(@NotNull Topics topics){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			if(this.topics.add(topics)){
				send(new ListenTopicRequest(topics));
			}
		}
	}
	
	private void send(@NotNull TwitchWebSocketRequest request){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			var data = JacksonUtils.writeAsString(request);
			log.trace("Sending WebSocket message: {}", data);
			send(data);
		}
		catch(JsonProcessingException e){
			log.error("Failed to convert WebSocket message to json", e);
		}
	}
	
	public void addListener(@NotNull TwitchWebSocketListener listener){
		listeners.add(listener);
	}
	
	public boolean isTopicListened(@NotNull Topic topic){
		return topics.stream()
				.flatMap(t -> t.getTopics().stream())
				.anyMatch(t -> Objects.equals(t, topic));
	}
	
	public int getTopicCount(){
		return topics.stream().mapToInt(Topics::getTopicCount).sum();
	}
}
