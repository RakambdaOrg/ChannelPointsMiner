package fr.raksrinana.twitchminer.factory;

import fr.raksrinana.twitchminer.miner.EventLogger;
import fr.raksrinana.twitchminer.miner.IMiner;

public class EventLoggerFactory{
	public static EventLogger create(IMiner miner){
		return new EventLogger(miner);
	}
}
