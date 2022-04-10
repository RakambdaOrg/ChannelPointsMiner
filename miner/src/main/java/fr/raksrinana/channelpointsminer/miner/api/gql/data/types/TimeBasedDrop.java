package fr.raksrinana.channelpointsminer.miner.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	@NotNull
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
	@JsonProperty("preconditionDrops")
	@Nullable
	@Builder.Default
	private List<TimeBasedDrop> preconditionDrops = new ArrayList<>();
	@JsonProperty("benefitEdges")
	@Nullable
	@Builder.Default
	private List<DropBenefitEdge> benefitEdges = new ArrayList<>();
	@JsonProperty("requiredMinutesWatched")
	private int requiredMinutesWatched;
	@JsonProperty("self")
	@Nullable
	private TimeBasedDropSelfEdge self;
	@JsonProperty("campaign")
	@Nullable
	private DropCampaign campaign;
}
