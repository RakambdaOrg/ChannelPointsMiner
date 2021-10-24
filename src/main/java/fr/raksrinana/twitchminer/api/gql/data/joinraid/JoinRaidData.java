package fr.raksrinana.twitchminer.api.gql.data.joinraid;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.twitchminer.api.gql.data.types.JoinRaidPayload;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class JoinRaidData{
	@JsonProperty("joinRaid")
	@NotNull
	private JoinRaidPayload joinRaid;
}
