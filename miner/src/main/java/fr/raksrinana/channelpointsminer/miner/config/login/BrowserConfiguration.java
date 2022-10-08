package fr.raksrinana.channelpointsminer.miner.config.login;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
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
@JsonClassDescription("Login though controlled browser (selenium).")
public class BrowserConfiguration implements ILoginMethod{
	@NotNull
	@JsonProperty("driver")
	@JsonPropertyDescription("Driver to use. Default: CHROME")
	@Builder.Default
	private BrowserDriver driver = BrowserDriver.CHROME;
	@JsonProperty("headless")
	@JsonPropertyDescription("Run browser headless. Not recommended. Default: false")
	@Builder.Default
	private boolean headless = false;
	@JsonProperty("screenshots")
	@JsonPropertyDescription("Take screenshots on error. Default: false")
	@Builder.Default
	private boolean screenshots = false;
	@JsonProperty("disableShm")
	@JsonPropertyDescription("Disable SHM usage. Default: false")
	@Builder.Default
	private boolean disableShm = false;
	@JsonProperty("userAgent")
	@JsonPropertyDescription("User-Agent to use. Default: use controlled browser User-Agent")
	private String userAgent;
	@JsonProperty(value = "userDir", required = true)
	@JsonPropertyDescription("User directory to use. Should be a different one per account used to mine.")
	private String userDir;
	@JsonProperty("binary")
	@JsonPropertyDescription("Binary of the browser to use. Used only if not using a REMOTE_XXX driver.")
	private String binary;
	@JsonProperty("remoteHost")
	@JsonPropertyDescription("Remote host of the selenium grid. Must be defined if using REMOTE_XXX driver.")
	private String remoteHost;
}
