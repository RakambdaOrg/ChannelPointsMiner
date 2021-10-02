package fr.raksrinana.twitchminer.api.gql.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersistedQueryExtension{
	@JsonProperty("version")
	private int version;
	@JsonProperty("sha256Hash")
	@NotNull
	private String sha256Hash;
}
