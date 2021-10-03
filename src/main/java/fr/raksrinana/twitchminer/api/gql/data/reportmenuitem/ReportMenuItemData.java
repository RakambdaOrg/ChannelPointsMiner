package fr.raksrinana.twitchminer.api.gql.data.reportmenuitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.RequestInfo;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
