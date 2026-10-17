package fr.rakambda.channelpointsminer.miner.tests;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;

@Log4j2
public class WebsocketMockServer extends WebSocketServer{
	private static final int MESSAGE_TIMEOUT = 15;
	
	@Getter
	private final int port;
	@Getter
	private final Collection<String> receivedMessages;
	private final Map<String, String> answers;
	private final Set<WebSocket> openConnections;
	@Getter
	private volatile boolean receivedClose;
	@Getter
	private volatile boolean started;
	
	public WebsocketMockServer(int port){
		super(new InetSocketAddress(port));
		setReuseAddr(true);
		
		log.debug("Starting websocket mock server on port {}", port);
		this.port = port;
		
		receivedMessages = new ConcurrentLinkedQueue<>();
		answers = new ConcurrentHashMap<>();
		openConnections = ConcurrentHashMap.newKeySet();
		receivedClose = false;
		started = false;
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake){
		log.debug("WebSocket mock server received connection");
		openConnections.add(conn);
	}
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote){
		log.debug("WebSocket mock server closing");
		openConnections.remove(conn);
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
		log.debug("WebSocket mock server started");
		started = true;
	}
	
	@Override
	public void onWebsocketPing(WebSocket conn, Framedata f){
		super.onWebsocketPing(conn, f);
		receivedMessages.add("PING");
	}
	
	@Override
	public void onWebsocketPong(WebSocket conn, Framedata f){
		super.onWebsocketPong(conn, f);
		receivedMessages.add("PONG");
	}
	
	public void reset(){
		receivedMessages.clear();
		answers.clear();
		receivedClose = false;
	}
	
	public void removeClients(){
		getConnections().forEach(c -> c.close(CloseFrame.NORMAL));
		await("Clients closed").atMost(MESSAGE_TIMEOUT, TimeUnit.SECONDS).until(() -> openConnections.isEmpty() && getConnections().isEmpty());
	}
	
	public void send(String message){
		awaitConnection();
		broadcast(message, openConnections);
	}
	
	public void sendPing(){
		awaitConnection();
		openConnections.forEach(WebSocket::sendPing);
	}
	
	/**
	 * The handshake response is written to the client before the server registers the connection and calls {@link #onOpen(WebSocket, ClientHandshake)}.
	 * A client's connectBlocking can therefore return before the server knows about the connection, and anything sent in between would be lost.
	 */
	private void awaitConnection(){
		await("Connection await").atMost(MESSAGE_TIMEOUT, TimeUnit.SECONDS).until(() -> !openConnections.isEmpty());
	}
	
	public void awaitClose(){
		await("Close await").atMost(MESSAGE_TIMEOUT, TimeUnit.SECONDS).until(this::isReceivedClose);
	}
	
	public void awaitMessage(){
		awaitMessage(1);
	}
	
	public void awaitMessage(int count){
		await("Message await").atMost(MESSAGE_TIMEOUT, TimeUnit.SECONDS).until(() -> getReceivedMessages().size() >= count);
	}
	
	public void awaitNothing(){
		await("Nothing await").atMost(MESSAGE_TIMEOUT, TimeUnit.SECONDS).failFast(() -> getReceivedMessages().isEmpty());
	}
}
