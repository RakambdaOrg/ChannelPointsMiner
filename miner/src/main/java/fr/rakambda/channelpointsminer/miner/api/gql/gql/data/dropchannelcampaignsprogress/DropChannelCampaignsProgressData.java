package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropchannelcampaignsprogress;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsCampaign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class DropChannelCampaignsProgressData{
	@JsonProperty("channelDropCampaignsProgress")
	@NonNull
	private List<DropsCampaign> channelDropCampaignsProgress;
}
