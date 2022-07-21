package fr.raksrinana.channelpointsminer.miner.api.chat.ws;

import fr.raksrinana.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.raksrinana.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.raksrinana.channelpointsminer.miner.factory.TimeFactory;
import fr.raksrinana.channelpointsminer.miner.factory.TwitchWebSocketClientFactory;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;

@Log4j2
public class TwitchChatWebSocketPool implements AutoCloseable, ITwitchChatWebSocketListener, ITwitchChatClient{
	private static final int SOCKET_TIMEOUT_MINUTES = 5;
	
	private final Collection<TwitchChatWebSocketClient> clients;
	private final int maxTopicPerClient;
	private final TwitchLogin twitchLogin;
	
	public TwitchChatWebSocketPool(int maxTopicPerClient, @NotNull TwitchLogin twitchLogin){
		this.maxTopicPerClient = maxTopicPerClient;
		this.twitchLogin = twitchLogin;
		clients = new ArrayList<>();
	}
	
	public void checkStaleConnection(){
		clients.stream()
				.filter(client -> TimeFactory.now().isAfter(client.getLastPing().plus(SOCKET_TIMEOUT_MINUTES, MINUTES)))
				.forEach(client -> client.close(ABNORMAL_CLOSE, "Timeout reached"));
	}
	
	@Override
	public void onWebSocketClosed(@NotNull TwitchChatWebSocketClient client, int code, @Nullable String reason, boolean remote){
		clients.remove(client);
		if(code != NORMAL){
			client.getChannels().forEach(this::join);
		}
	}
	
	@Override
	public void join(@NotNull String channel){
		var lowerChannel = channel.toLowerCase();
		var isListened = isChannelJoined(lowerChannel);
		if(isListened){
			log.debug("Channel {} is already joined", lowerChannel);
			return;
		}
		getAvailableClient().join(lowerChannel);
	}
	
	@Override
	public void leave(@NotNull String channel){
		var lowerChannel = channel.toLowerCase();
		clients.stream()
				.filter(client -> client.isChannelJoined(lowerChannel))
				.forEach(client -> client.leave(lowerChannel));
	}
	
	private boolean isChannelJoined(@NotNull String channel){
		return clients.stream().anyMatch(client -> client.isChannelJoined(channel));
	}
	
	@NotNull
	private TwitchChatWebSocketClient getAvailableClient(){
		return clients.stream()
				.filter(client -> client.getChannelCount() < maxTopicPerClient)
				.findAny()
				.orElseGet(this::createNewClient);
	}
	
	@NotNull
	private TwitchChatWebSocketClient createNewClient(){
		try{
			var client = TwitchWebSocketClientFactory.createChatClient(twitchLogin);
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
	
	@Override
	public void close(){
		clients.forEach(WebSocketClient::close);
	}
	
	public int getClientCount(){
		return clients.size();
	}
}
