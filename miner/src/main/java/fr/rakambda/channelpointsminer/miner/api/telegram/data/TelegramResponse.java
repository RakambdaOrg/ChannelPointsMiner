package fr.rakambda.channelpointsminer.miner.api.telegram.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@ToString
public class TelegramResponse {
	@JsonProperty("ok")
	private boolean success;
	@JsonProperty("description")
	private String description;
}
