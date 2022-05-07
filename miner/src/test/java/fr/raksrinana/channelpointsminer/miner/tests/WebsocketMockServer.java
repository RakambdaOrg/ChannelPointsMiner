package fr.raksrinana.channelpointsminer.miner.tests;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;

@Log4j2
public class WebsocketMockServer extends WebSocketServer{
	private static final int MESSAGE_TIMEOUT = 15;
	
	@Getter
	private final int port;
	@Getter
	private final ArrayList<String> receivedMessages;
	private final Map<String, String> answers;
	@Getter
	private boolean receivedClose;
	
	public WebsocketMockServer(int port){
		super(new InetSocketAddress(port));
		setReuseAddr(true);
		
		log.debug("Starting websocket mock server on port {}", port);
		
		this.port = port;
		
		receivedMessages = new ArrayList<>();
		answers = new HashMap<>();
		receivedClose = false;
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake){
	}
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote){
		receivedClose = true;
	}
	
	@Override
	public void onMessage(WebSocket conn, String message){
		receivedMessages.add(message);
		var answer = answers.get(message);
		if(Objects.nonNull(answer)){
			conn.send(answer);
		}
	}
	
	@Override
	public void onError(WebSocket conn, Exception ex){
		log.error("Mock ws server exception", ex);
	}
	
	@Override
	public void onStart(){
	}
	
	public void reset(){
		receivedMessages.clear();
		answers.clear();
		receivedClose = false;
	}
	
	public void removeClients(){
		getConnections().forEach(c -> c.close(CloseFrame.NORMAL));
	}
	
	public void send(String message){
		broadcast(message);
	}
	
	public void awaitMessage(){
		await("Message await").atMost(MESSAGE_TIMEOUT, TimeUnit.SECONDS).until(() -> !getReceivedMessages().isEmpty());
	}
	
	public void awaitNothing(){
		await("Nothing await").atMost(MESSAGE_TIMEOUT, TimeUnit.SECONDS).failFast(() -> getReceivedMessages().isEmpty());
	}
}
