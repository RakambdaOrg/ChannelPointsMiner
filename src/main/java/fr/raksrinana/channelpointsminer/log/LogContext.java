package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.apache.logging.log4j.CloseableThreadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LogContext implements AutoCloseable{
	private static final String STREAMER_NAME_KEY = "streamer_name";
	private static final String WEBSOCKET_ID_KEY = "websocket_id";
	private static final String WEBSOCKET_TOPIC = "websocket_topic";
	private static final String EVENT_ID = "event_id";
	
	private final CloseableThreadContext.Instance ctc;
	
	private LogContext(@Nullable Streamer streamer){
		if(Objects.nonNull(streamer)){
			ctc = CloseableThreadContext.put(STREAMER_NAME_KEY, streamer.getUsername());
		}
		else{
			ctc = CloseableThreadContext.putAll(Map.of());
		}
	}
	
	public LogContext(@NotNull Map<String, String> values, @NotNull List<String> messages){
		ctc = CloseableThreadContext.putAll(values).pushAll(messages);
	}
	
	@NotNull
	public static LogContext empty(){
		return new LogContext(null);
	}
	
	@NotNull
	public static LogContext restore(@NotNull Map<String, String> values, @NotNull List<String> messages){
		return new LogContext(values, messages);
	}
	
	@NotNull
	public static LogContext with(@Nullable Streamer streamer){
		return new LogContext(streamer);
	}
	
	@NotNull
	public LogContext withSocketId(@NotNull String uuid){
		ctc.put(WEBSOCKET_ID_KEY, uuid);
		return this;
	}
	
	@NotNull
	public LogContext withTopic(@NotNull Topic topic){
		ctc.put(WEBSOCKET_TOPIC, topic.getValue());
		return this;
	}
	
	@NotNull
	public LogContext withEventId(@NotNull String eventId){
		ctc.put(EVENT_ID, eventId);
		return this;
	}
	
	@Override
	public void close(){
		ctc.close();
	}
}
