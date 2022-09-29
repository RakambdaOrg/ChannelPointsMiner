package fr.raksrinana.channelpointsminer.miner.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BrowserConfiguration{
	@NotNull
	private BrowserDriver driver = BrowserDriver.CHROME;
	private boolean headless = false;
	private boolean screenshots = false;
	private boolean disableShm = false;
	private String userAgent;
	private String userDir;
	private String binary;
	private String remoteHost;
}
