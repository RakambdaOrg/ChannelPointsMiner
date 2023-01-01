package fr.rakambda.channelpointsminer.miner.event;

import org.apache.commons.text.lookup.StringLookup;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public interface ILoggableEvent extends IEvent, StringLookup{
	@NotNull
	String getConsoleLogFormat();
	
	@NotNull
	String getDefaultFormat();
	
	@NotNull
	Map<String, String> getEmbedFields();
}
