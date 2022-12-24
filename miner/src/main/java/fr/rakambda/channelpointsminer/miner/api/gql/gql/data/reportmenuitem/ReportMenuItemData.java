package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.reportmenuitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.RequestInfo;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ReportMenuItemData{
	@JsonProperty("requestInfo")
	@NotNull
	private RequestInfo requestInfo;
	@JsonProperty("user")
	@NotNull
	private User user;
}
