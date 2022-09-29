package fr.raksrinana.channelpointsminer.miner.factory;

import fr.raksrinana.channelpointsminer.miner.api.gql.integrity.browser.Browser;
import fr.raksrinana.channelpointsminer.miner.config.BrowserConfiguration;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
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