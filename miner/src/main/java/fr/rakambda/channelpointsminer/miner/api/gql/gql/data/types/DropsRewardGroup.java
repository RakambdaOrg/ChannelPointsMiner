package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.annotation.JsonDeserialize;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
	@NonNull
	private DropsRewardGroupSelfEdge self;
}
