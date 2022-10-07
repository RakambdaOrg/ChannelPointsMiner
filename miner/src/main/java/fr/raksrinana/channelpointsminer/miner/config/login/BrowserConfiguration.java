package fr.raksrinana.channelpointsminer.miner.config.login;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.miner.config.BrowserDriver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("browser")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrowserConfiguration implements ILoginMethod{
	@NotNull
	@Builder.Default
	private BrowserDriver driver = BrowserDriver.CHROME;
	@Builder.Default
	private boolean headless = false;
	@Builder.Default
	private boolean screenshots = false;
	@Builder.Default
	private boolean disableShm = false;
	private String userAgent;
	private String userDir;
	private String binary;
	private String remoteHost;
}
