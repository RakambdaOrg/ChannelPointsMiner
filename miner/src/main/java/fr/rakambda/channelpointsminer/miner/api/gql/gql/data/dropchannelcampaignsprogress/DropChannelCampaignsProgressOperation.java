package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropchannelcampaignsprogress;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.dropshighlightserviceavailabledrops.DropsHighlightServiceAvailableDropsData;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropChannelCampaignsProgressOperation extends IGQLOperation<DropChannelCampaignsProgressData>{
    public DropChannelCampaignsProgressOperation(@NonNull String channelId){
        super("DropChannelCampaignsProgress");
	    addPersistedQueryExtension(new PersistedQueryExtension(1, "a9778563e6f7ef878891dcc4049c263ec376c1ef5c1ad69395c3d8aef7f455e4"));
        addVariable("channelID", channelId);
    }
    
    @Override
    @NonNull
    public GenericType<GQLResponse<DropChannelCampaignsProgressData>> getResponseType(){
        return new GenericType<>(){};
    }
}
