package fr.raksrinana.twitchminer.api.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.twitchminer.api.ws.data.request.ListenTopicRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.PingRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.TwitchWebSocketRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.api.ws.data.response.PongResponse;
import fr.raksrinana.twitchminer.api.ws.data.response.ResponseResponse;
import fr.raksrinana.twitchminer.api.ws.data.response.TwitchWebSocketResponse;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;

@Log4j2
public class TwitchWebSocketClient extends WebSocketClient{
	private static final URI WEBSOCKET_URI = URI.create("wss://pubsub-edge.twitch.tv/v1");
	
	@Getter
	private final Set<Topics> topics;
	private final List<TwitchWebSocketListener> listeners;
	private long lastPing;
	@Getter
	private long lastPong;
	
	public TwitchWebSocketClient(){
		super(WEBSOCKET_URI);
		setConnectionLostTimeout(0);
		topics = new HashSet<>();
		listeners = new ArrayList<>();
		lastPing = 0;
		lastPong = 0;
	}
	
	@Override
	public void onOpen(ServerHandshake handshakedata){
		log.info("WebSocket opened");
		ping();
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote){
		log.info("WebSocket closed with code {}, from host {}, reason {}", code, remote, reason);
		listeners.forEach(l -> l.onWebSocketClosed(this, code, reason, remote));
	}
	
	@Override
	public void onMessage(String messageStr){
		try{
			log.trace("Received Websocket message: {}", messageStr);
			var message = JacksonUtils.read(messageStr, new TypeReference<TwitchWebSocketResponse>(){});
			log.info("Parsed message: {}", message);
			
			if(message instanceof ResponseResponse responseMessage){
				if(responseMessage.hasError()){
					if(Objects.equals("ERR_BADAUTH", responseMessage.getError())){
						close(ABNORMAL_CLOSE, "Invalid credentials");
					}
					else{
						log.error("Received error response {}", responseMessage);
					}
				}
			}
			if(message instanceof PongResponse){
				onPong();
			}
			listeners.forEach(listener -> listener.onWebSocketMessage(message));
		}
		catch(Exception e){
			log.error("Failed to handle WebSocket message", e);
		}
	}
	
	@Override
	public void onMessage(ByteBuffer message){
		log.trace("Received WebSocket byte buffer");
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
		lastPong = System.currentTimeMillis();
	}
	
	public void ping(){
		send(new PingRequest());
		lastPing = System.currentTimeMillis();
	}
	
	private void send(@NotNull TwitchWebSocketRequest request){
		try{
			var data = JacksonUtils.writeAsString(request);
			log.trace("Sending WebSocket message: {}", data);
			send(data);
		}
		catch(JsonProcessingException e){
			log.error("Failed to convert WebSocket message to json", e);
		}
	}
	
	public void listenTopic(@NotNull Topics topics){
		if(this.topics.add(topics)){
			send(new ListenTopicRequest(topics));
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
