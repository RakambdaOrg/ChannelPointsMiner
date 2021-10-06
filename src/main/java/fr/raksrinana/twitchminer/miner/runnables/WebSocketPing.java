package fr.raksrinana.twitchminer.miner.runnables;

import fr.raksrinana.twitchminer.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@RequiredArgsConstructor
public class WebSocketPing implements Runnable{
	@NotNull
	private final IMiner miner;
	
	@Override
	public void run(){
		miner.getWebSocketPool().ping();
	}
}
