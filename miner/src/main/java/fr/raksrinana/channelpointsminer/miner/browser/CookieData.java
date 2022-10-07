package fr.raksrinana.channelpointsminer.miner.browser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.SecondsTimestampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class CookieData{
	@JsonProperty("domain")
	private String domain;
	@JsonProperty("expirationDate")
	@JsonDeserialize(using = SecondsTimestampDeserializer.class)
	private Instant expirationDate;
	@JsonProperty("hostOnly")
	private boolean hostOnly;
	@JsonProperty("httpOnly")
	private boolean httpOnly;
	@JsonProperty("name")
	private String name;
	@JsonProperty("path")
	private String path;
	@JsonProperty("sameSite")
	private String sameSite;
	@JsonProperty("secure")
	private boolean secure;
	@JsonProperty("session")
	private boolean session;
	@JsonProperty("storeId")
	private String storeId;
	@JsonProperty("value")
	private String value;
	
	@Nullable
	public Date getExpiry(){
		return Optional.ofNullable(getExpirationDate())
				.map(Date::from)
				.orElse(null);
	}
}