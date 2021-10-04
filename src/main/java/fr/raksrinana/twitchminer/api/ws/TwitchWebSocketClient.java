package fr.raksrinana.twitchminer.api.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.twitchminer.Main;
import fr.raksrinana.twitchminer.api.ws.data.request.ListenTopicRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.TwitchWebSocketRequest;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.TopicName;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.api.ws.data.response.TwitchWebSocketResponse;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
public class TwitchWebSocketClient extends WebSocketClient{
	private static final URI WEBSOCKET_URI = URI.create("wss://pubsub-edge.twitch.tv/v1");
	
	private final List<Topics> topics;
	private final List<TwitchWebSocketListener> listeners;
	
	public TwitchWebSocketClient(){
		super(WEBSOCKET_URI);
		setConnectionLostTimeout(180);
		topics = new ArrayList<>();
		listeners = new ArrayList<>();
	}
	
	@Override
	public void onOpen(ServerHandshake handshakedata){
		log.info("WebSocket opened");
		ping();
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote){
		log.info("WebSocket closed with code {}, from host {}, reason {}", code, remote, reason);
	}
	
	@Override
	public void onMessage(String messageStr){
		try{
			log.trace("Received Websocket message: {}", messageStr);
			var message = JacksonUtils.read(messageStr, new TypeReference<TwitchWebSocketResponse>(){});
			log.info("Parsed message: {}", message);
			
			listeners.forEach(listener -> listener.onMessage(message));
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
	
	public void ping(){
		sendPing();
	}
	
	public void listenTopic(@NotNull TopicName name, @NotNull String target){
		listenTopic(Topics.buildFromName(name, target, Main.getTwitchLogin().getAccessToken()));
	}
	
	public void listenTopic(@NotNull Topics topics){
		this.topics.add(topics);
		send(new ListenTopicRequest(topics));
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
