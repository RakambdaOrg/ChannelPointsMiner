package fr.raksrinana.channelpointsminer.api.gql.data.channelfollows;

import fr.raksrinana.channelpointsminer.api.gql.data.GQLOperation;
import fr.raksrinana.channelpointsminer.api.gql.data.GQLResponse;
import fr.raksrinana.channelpointsminer.api.gql.data.PersistedQueryExtension;
import kong.unirest.GenericType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ChannelFollowsOperation extends GQLOperation<ChannelFollowsData>{
	public ChannelFollowsOperation(int limit, @NotNull String order, @Nullable String cursor){
		super("ChannelFollows");
		addPersistedQueryExtension(new PersistedQueryExtension(1, "4b9cb31b54b9213e5760f2f6e9e935ad09924cac2f78aac51f8a64d85f028ed0"));
		addVariable("limit", limit);
		addVariable("order", order);
		if(Objects.nonNull(cursor)){
			addVariable("cursor", cursor);
		}
	}
	
	@Override
	@NotNull
	public GenericType<GQLResponse<ChannelFollowsData>> getResponseType(){
		return new GenericType<>(){};
	}
}
