package fr.rakambda.channelpointsminer.miner.runnable;

import fr.rakambda.channelpointsminer.miner.log.LogContext;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;

@Log4j2
@RequiredArgsConstructor
public class HermesWebSocketPing implements Runnable{
	@NonNull
	private final IMiner miner;
	
	@Override
	public void run(){
		try(var ignored = LogContext.with(miner)){
			miner.getHermesWebSocketPool().ping();
			miner.getHermesWebSocketPool().listenPendingPubSubTopics();
		}
	}
}
