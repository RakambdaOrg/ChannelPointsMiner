package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.browser.Browser;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BrowserFactory{
	@NotNull
	public static Browser createBrowser(@NotNull BrowserConfiguration configuration){
		return new Browser(configuration);
	}
}
