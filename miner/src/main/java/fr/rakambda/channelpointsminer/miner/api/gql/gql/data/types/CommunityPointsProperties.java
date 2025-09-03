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
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("CommunityPointsProperties")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsProperties extends GQLType{
	@JsonProperty("availableClaim")
	@Nullable
	private CommunityPointsClaim availableClaim;
	@JsonProperty("balance")
	private int balance;
	@JsonProperty("activeMultipliers")
	@NonNull
	@Builder.Default
	private List<CommunityPointsMultiplier> activeMultipliers = new ArrayList<>();
}
