package fr.raksrinana.twitchminer.api.gql.data.claimcommunitypoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class InputData{
	@JsonProperty("channelID")
	private String channelId;
	@JsonProperty("claimID")
	private String claimId;
}
