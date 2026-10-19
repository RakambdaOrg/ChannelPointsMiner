package fr.rakambda.channelpointsminer.miner.priority;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropchannelcampaignsprogress.DropChannelCampaignsProgressData;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsCampaign;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsProgressCriteria;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsRewardGroup;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types.DropsRewardGroupSelfEdge;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.miner.IMiner;
import fr.rakambda.channelpointsminer.miner.streamer.Streamer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@JsonTypeName("drops")
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Log4j2
@JsonClassDescription("Return a score if a drop campaign may be progressed by watching this stream.")
public class DropsPriority extends IStreamerPriority{
	@Override
	public int getScore(@NonNull IMiner miner, @NonNull Streamer streamer){
		if(streamer.isParticipateCampaigns()
				&& streamer.isStreamingGame()
				&& hasCampaigns(streamer)){
			return getScore();
		}
		
		return 0;
	}
	
	@Override
	public boolean isDropsRelated(){
		return true;
	}
	
	private boolean hasCampaigns(@NonNull Streamer streamer){
		return Optional.ofNullable(streamer.getDropChannelCampaignsProgress())
				.stream()
				.map(DropChannelCampaignsProgressData::getChannelDropCampaignsProgress)
				.flatMap(Collection::stream)
				.anyMatch(dropsCampaign -> isValidCampaign(streamer, dropsCampaign));
	}
	
	private boolean isValidCampaign(@NonNull Streamer streamer, @NonNull DropsCampaign dropsCampaign){
		var now = TimeFactory.nowZoned();
		
		if(Optional.ofNullable(dropsCampaign.getStartAt()).map(date -> date.isAfter(now)).orElse(false)){
			log.trace("Campaign {} hasn't started", dropsCampaign.getId());
			return false;
		}
		if(Optional.ofNullable(dropsCampaign.getEndAt()).map(date -> date.isBefore(now)).orElse(false)){
			log.trace("Campaign {} already ended", dropsCampaign.getId());
			return false;
		}
		if(streamer.isExcludeSubscriberDrops()
				&& Optional.ofNullable(dropsCampaign.getRewardGroups()).stream()
				.flatMap(Collection::stream)
				.map(DropsRewardGroup::getProgressCriteria)
				.map(DropsProgressCriteria::getRequirementType)
				.anyMatch(d -> Objects.equals(d, "SUB"))){
			log.trace("Campaign {} requires subscriptions", dropsCampaign.getId());
			return false;
		}
		
		var result = dropsCampaign.getRewardGroups().stream()
				.map(DropsRewardGroup::getSelf)
				.filter(Objects::nonNull)
				.map(DropsRewardGroupSelfEdge::getStatus)
				.anyMatch(d -> Objects.equals(d, "IN_PROGRESS"));
		if(!result){
			log.trace("Campaign {} has no valid drops", dropsCampaign.getId());
		}
		return result;
	}
}
