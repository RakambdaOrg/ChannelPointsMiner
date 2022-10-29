package fr.rakambda.channelpointsminer.miner.api.chat.ws;

import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatClient;
import fr.rakambda.channelpointsminer.miner.api.chat.ITwitchChatMessageListener;
import fr.rakambda.channelpointsminer.miner.api.passport.TwitchLogin;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.LogContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

@Log4j2
public class TwitchChatWebSocketClient extends WebSocketClient implements ITwitchChatClient{
	private final static Pattern MESSAGE_PATTERN = Pattern.compile("badges=([^;]*);.*display-name=([^;]*);.*PRIVMSG #([^ ]*) :(.*)");
	
	@Getter
	private final Set<String> channels;
	private final Collection<ITwitchChatWebSocketClosedListener> socketClosedListeners;
	@Getter
	private final String uuid;
	private final TwitchLogin twitchLogin;
	private final Collection<ITwitchChatMessageListener> chatMessageListeners;
	@Setter(onMethod_ = {@TestOnly}, value = AccessLevel.PROTECTED)
	private boolean listenMessages;
	
	@Getter
	private Instant lastHeartbeat;
	
	public TwitchChatWebSocketClient(@NotNull URI uri, @NotNull TwitchLogin twitchLogin, boolean listenMessages){
		super(uri);
		this.twitchLogin = twitchLogin;
		this.listenMessages = listenMessages;
		uuid = UUID.randomUUID().toString();
		
		setConnectionLostTimeout(0);
		channels = new HashSet<>();
		socketClosedListeners = new ConcurrentLinkedQueue<>();
		chatMessageListeners = new LinkedList<>();
		lastHeartbeat = Instant.EPOCH;
	}
	
	@Override
	public void onOpen(ServerHandshake serverHandshake){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.info("Chat WebSocket opened");
			onHeartbeat();
			sendMessage("CAP REQ :twitch.tv/tags twitch.tv/commands");
			send("PASS oauth:%s".formatted(twitchLogin.getAccessToken()));
			sendMessage("NICK %s".formatted(twitchLogin.getUsername().toLowerCase()));
		}
	}
	
	@Override
	public void onMessage(String messageStr){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.trace("Received Chat Websocket message: {}", messageStr.strip());
			if(messageStr.startsWith("PONG :tmi.twitch.tv")){
				onWebsocketPong(this, new PongFrame());
			}
			else if(listenMessages){
				var messageMatch = MESSAGE_PATTERN.matcher(messageStr);
				if(messageMatch.find()){
					var badges = messageMatch.group(1);
					var actor = messageMatch.group(2);
					var streamer = messageMatch.group(3);
					var message = messageMatch.group(4);
					chatMessageListeners.forEach(l -> l.onChatMessage(streamer, actor, message, badges));
				}
			}
		}
		catch(Exception e){
			log.error("Failed to handle Chat WebSocket message {}", messageStr, e);
		}
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.info("Chat WebSocket closed with code {}, from host {}, reason {}", code, remote, reason);
			socketClosedListeners.forEach(l -> l.onWebSocketClosed(this, code, reason, remote));
		}
	}
	
	@Override
	public void onError(Exception e){
		log.error("Error from Chat WebSocket", e);
	}
	
	private void onHeartbeat(){
		lastHeartbeat = TimeFactory.now();
		log.debug("Received WS Chat heartbeat");
	}
	
	private void sendMessage(@NotNull String message){
		log.trace("Sending Chat message {}", message);
		send(message);
	}
	
	@Override
	public void onWebsocketPing(WebSocket conn, Framedata f){
		onHeartbeat();
		sendMessage("PONG");
	}
	
	@Override
	public void onWebsocketPong(WebSocket conn, Framedata f){
		onHeartbeat();
	}
	
	@Override
	public void join(@NotNull String channel){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			if(channels.add(channel)){
				log.info("Joining Chat channel {}", channel);
				sendMessage("JOIN #" + channel);
			}
		}
	}
	
	@Override
	public void joinPending(){
	}
	
	@Override
	public void leave(@NotNull String channel){
		try(var ignored = LogContext.empty().withSocketId(uuid)){
			log.info("Leaving Chat channel {}", channel);
			sendMessage("PART #" + channel);
			channels.remove(channel);
		}
	}
	
	@Override
	public void ping(){
		send("PING");
	}
	
	@Override
	public void addChatMessageListener(@NotNull ITwitchChatMessageListener listener){
		chatMessageListeners.add(listener);
	}
	
	public boolean isChannelJoined(@NotNull String channel){
		return channels.contains(channel);
	}
	
	public void addWebSocketClosedListener(@NotNull ITwitchChatWebSocketClosedListener listener){
		socketClosedListeners.add(listener);
	}
	
	public long getChannelCount(){
		return getChannels().size();
	}
}
