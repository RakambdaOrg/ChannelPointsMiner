package fr.raksrinana.twitchminer.api.gql.data.response.reportmenuitem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.response.types.RequestInfo;
import fr.raksrinana.twitchminer.api.gql.data.response.types.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ReportMenuItemData{
	@JsonProperty("requestInfo")
	@NotNull
	private RequestInfo requestInfo;
	@JsonProperty("user")
	@NotNull
	private User user;
}
