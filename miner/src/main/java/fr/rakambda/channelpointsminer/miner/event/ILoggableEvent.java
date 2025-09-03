package fr.rakambda.channelpointsminer.miner.event;

import org.apache.commons.text.lookup.StringLookup;
import org.jspecify.annotations.NonNull;
import java.util.Map;

public interface ILoggableEvent extends IEvent, StringLookup{
	@NonNull
	String getConsoleLogFormat();
	
	@NonNull
	String getDefaultFormat();
	
	@NonNull
	Map<String, String> getEmbedFields();
}
