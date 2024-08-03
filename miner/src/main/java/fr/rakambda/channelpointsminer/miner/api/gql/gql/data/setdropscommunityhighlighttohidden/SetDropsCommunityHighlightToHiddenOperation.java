package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.setdropscommunityhighlighttohidden;

import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.GQLResponse;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.IGQLOperation;
import fr.rakambda.channelpointsminer.miner.api.gql.gql.data.PersistedQueryExtension;
import kong.unirest.core.GenericType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class SetDropsCommunityHighlightToHiddenOperation extends IGQLOperation<SetDropsCommunityHighlightToHiddenData>{
	public SetDropsCommunityHighlightToHiddenOperation(@NotNull String channelId, @NotNull String campaignID){
		super("SetDropsCommunityHighlightToHidden");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "9dfaa13dbc7d178a35c0a038a270fad4b7bc1d0e1d404a18aed9b26ee797a697"));
		addVariable("input", InputData.builder().channelID(channelId).campaignID(campaignID).build());
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<SetDropsCommunityHighlightToHiddenData>> getResponseType(){
		return new GenericType<>(){};
	}
}
