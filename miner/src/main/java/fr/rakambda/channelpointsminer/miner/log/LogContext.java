package fr.rakambda.channelpointsminer.miner.log;

import fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic.Topic;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import org.apache.logging.log4j.CloseableThreadContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LogContext implements AutoCloseable{
	private static final String ACCOUNT_NAME_KEY = "account_name";
	private static final String STREAMER_NAME_KEY = "streamer_name";
	private static final String WEBSOCKET_ID_KEY = "websocket_id";
	private static final String WEBSOCKET_TOPIC = "websocket_topic";
	private static final String EVENT_ID = "event_id";
	
	private final CloseableThreadContext.Instance ctc;
	
	private LogContext(@Nullable String accountName){
		if(Objects.nonNull(accountName)){
			ctc = CloseableThreadContext.put(ACCOUNT_NAME_KEY, accountName);
		}
		else{
			ctc = CloseableThreadContext.putAll(Map.of());
		}
	}
	
	public LogContext(@NonNull Map<String, String> values, @NonNull List<String> messages){
		ctc = CloseableThreadContext.putAll(values).pushAll(messages);
	}
	
	@NonNull
	public static LogContext empty(){
		return new LogContext(null);
	}
	
	@NonNull
	public static LogContext restore(@NonNull Map<String, String> values, @NonNull List<String> messages){
		return new LogContext(values, messages);
	}
	
	public static LogContext with(@Nullable IMiner miner){
		return with(Optional.ofNullable(miner).map(IMiner::getUsername).orElse(null));
	}
	
	public static LogContext with(@Nullable String accountName){
		return new LogContext(accountName);
	}
	
	@NonNull
	public LogContext withStreamer(@Nullable Streamer streamer){
		if(Objects.nonNull(streamer)){
			return withStreamer(streamer.getUsername());
		}
		return this;
	}
	
	@NonNull
	public LogContext withStreamer(@Nullable String streamer){
		if(Objects.nonNull(streamer)){
			ctc.put(STREAMER_NAME_KEY, streamer);
		}
		return this;
	}
	
	@NonNull
	public LogContext withSocketId(@NonNull String uuid){
		ctc.put(WEBSOCKET_ID_KEY, uuid);
		return this;
	}
	
	@NonNull
	public LogContext withTopic(@NonNull Topic topic){
		ctc.put(WEBSOCKET_TOPIC, topic.getValue());
		return this;
	}
	
	@NonNull
	public LogContext withEventId(@NonNull String eventId){
		ctc.put(EVENT_ID, eventId);
		return this;
	}
	
	@Override
	public void close(){
		ctc.close();
	}
}
