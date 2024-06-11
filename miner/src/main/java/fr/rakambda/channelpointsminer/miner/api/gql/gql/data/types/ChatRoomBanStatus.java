package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("ChatRoomBanStatus")
@Getter
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ChatRoomBanStatus extends GQLType{
}
