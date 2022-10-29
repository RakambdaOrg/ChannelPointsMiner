package fr.rakambda.channelpointsminer.miner.factory;

import fr.rakambda.channelpointsminer.miner.browser.Browser;
import fr.rakambda.channelpointsminer.miner.config.login.BrowserConfiguration;
import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
@ExtendWith(MockitoExtension.class)
class BrowserFactoryTest{
	@Mock
	private BrowserConfiguration configuration;
	
	@Test
	void createBrowser(){
		assertThat(BrowserFactory.createBrowser(configuration)).isNotNull().isInstanceOf(Browser.class);
	}
}