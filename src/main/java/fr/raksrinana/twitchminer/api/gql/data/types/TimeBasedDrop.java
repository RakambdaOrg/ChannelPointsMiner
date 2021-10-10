package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
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
	@NotNull
	private String name;
	@JsonProperty("startAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime startAt;
	@JsonProperty("endAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime endAt;
	@JsonProperty("benefitEdges")
	@NotNull
	@Builder.Default
	private List<DropBenefitEdge> benefitEdges = new ArrayList<>();
	@JsonProperty("requiredMinutesWatched")
	private int requiredMinutesWatched;
}
