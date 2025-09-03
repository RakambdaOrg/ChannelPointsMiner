package fr.rakambda.channelpointsminer.miner.api.gql.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("TimeBasedDrop")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class TimeBasedDrop extends GQLType{
	@JsonProperty("id")
	@NonNull
	private String id;
	@JsonProperty("name")
	@Nullable
	private String name;
	@JsonProperty("startAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime startAt;
	@JsonProperty("endAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endAt;
	@JsonProperty("benefitEdges")
	@Nullable
	@Builder.Default
	private List<DropBenefitEdge> benefitEdges = new ArrayList<>();
	@JsonProperty("self")
	@Nullable
	private TimeBasedDropSelfEdge self;
}
