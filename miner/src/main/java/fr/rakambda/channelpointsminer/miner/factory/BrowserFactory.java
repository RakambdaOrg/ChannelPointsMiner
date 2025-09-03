package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.browser.Browser;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.event.manager.IEventManager;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BrowserFactory{
	@NonNull
	public static Browser createBrowser(@NonNull BrowserConfiguration configuration, @NonNull IEventManager eventManager){
		return new Browser(configuration, eventManager);
	}
}
