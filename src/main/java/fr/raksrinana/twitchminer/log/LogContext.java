package fr.raksrinana.twitchminer.log;

import fr.raksrinana.twitchminer.miner.streamer.Streamer;
import org.apache.logging.log4j.CloseableThreadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LogContext implements AutoCloseable{
	private static final String STREAMER_NAME_KEY = "streamer_name";
	
	private final CloseableThreadContext.Instance ctc;
	
	private LogContext(@Nullable Streamer streamer){
		if(Objects.nonNull(streamer)){
			ctc = CloseableThreadContext.put(STREAMER_NAME_KEY, streamer.getUsername());
		}
		else{
			ctc = CloseableThreadContext.push(STREAMER_NAME_KEY);
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
	public static LogContext with(@Nullable Streamer streamer){
		return new LogContext(streamer);
	}
	
	@NotNull
	public static LogContext restore(@NotNull Map<String, String> values, @NotNull List<String> messages){
		return new LogContext(values, messages);
	}
	
	@Override
	public void close(){
		if(Objects.nonNull(ctc)){
			ctc.close();
		}
	}
}
