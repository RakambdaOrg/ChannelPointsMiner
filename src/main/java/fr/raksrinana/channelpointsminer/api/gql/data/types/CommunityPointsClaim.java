package fr.raksrinana.channelpointsminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("CommunityPointsClaim")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommunityPointsClaim extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("multipliers")
	@NotNull
	@Builder.Default
	private List<CommunityPointsMultiplier> multipliers = new ArrayList<>();
	@JsonProperty("pointsEarnedBaseline")
	private int pointsEarnedBaseline;
	@JsonProperty("pointsEarnedTotal")
	private int pointsEarnedTotal;
}
