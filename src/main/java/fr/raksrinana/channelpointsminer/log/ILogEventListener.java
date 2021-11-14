package fr.raksrinana.channelpointsminer.log;

import fr.raksrinana.channelpointsminer.log.event.ILogEvent;

public interface ILogEventListener{
	void onLogEvent(ILogEvent event);
}
