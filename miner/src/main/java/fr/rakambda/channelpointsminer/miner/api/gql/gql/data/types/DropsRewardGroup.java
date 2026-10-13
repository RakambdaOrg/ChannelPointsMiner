package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JsonTypeName("DropsRewardGroup")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropsRewardGroup extends GQLType{
	@JsonProperty("id")
	@NonNull
	private String id;
	@JsonProperty("progressCriteria")
	@NonNull
	private DropsProgressCriteria progressCriteria;
	@JsonProperty("self")
	@Nullable
	private DropsRewardGroupSelfEdge self;
}
