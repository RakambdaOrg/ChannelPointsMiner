package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.twitchminer.util.json.URLDeserializer;
import fr.raksrinana.twitchminer.util.json.UnknownDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("DropCampaign")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class DropCampaign extends GQLType{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("name")
	@Nullable
	private String name;
	@JsonProperty("game")
	@Nullable
	private Game game;
	@JsonProperty("detailsURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL detailsUrl;
	@JsonProperty("startAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime startAt;
	@JsonProperty("endAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endAt;
	@JsonProperty("imageURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL imageUrl;
	@JsonProperty("accountLinkURL")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL accountLinkUrl;
	@JsonProperty("eventBasedDrops")
	@JsonDeserialize(contentUsing = UnknownDeserializer.class)
	@NotNull
	@Builder.Default
	private List<Object> eventBasedDrops = new ArrayList<>();
	@JsonProperty("timeBasedDrops")
	@NotNull
	@Builder.Default
	private List<TimeBasedDrop> timeBasedDrops = new ArrayList<>();
	@JsonProperty("status")
	@Nullable
	private DropCampaignStatus status;
	@JsonProperty("self")
	@Nullable
	private DropCampaignSelfEdge self;
	@JsonProperty("allow")
	@Nullable
	private DropCampaignACL allow;
}
