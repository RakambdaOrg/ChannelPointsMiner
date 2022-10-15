package fr.raksrinana.channelpointsminer.miner.api.ws.data.message.createnotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.miner.api.ws.data.message.subtype.NotificationDisplayType;
import fr.raksrinana.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class Notification{
	@JsonProperty("user_id")
	@NotNull
	private String userId;
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("body")
	@NotNull
	private String body;
	@JsonProperty("body_md")
	@NotNull
	private String bodyMd;
	@JsonProperty("type")
	@NotNull
	private String type;
	@JsonProperty("render_style")
	@Nullable
	private String renderStyle;
	@JsonProperty("thumbnail_url")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL thumbnailUrl;
	@JsonProperty("actions")
	@NotNull
	@Builder.Default
	private List<NotificationAction> actions = new ArrayList<>();
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime createdAt;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime updatedAt;
	@JsonProperty("read")
	private boolean read;
	@JsonProperty("display_type")
	@NotNull
	private NotificationDisplayType displayType;
	@JsonProperty("category")
	@Nullable
	private String category;
	@JsonProperty("mobile_destination_type")
	@NotNull
	private String mobileDestinationType;
	@JsonProperty("mobile_destination_key")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL mobileDestinationKey;
	@JsonProperty("data_blocks")
	@NotNull
	@Builder.Default
	private List<NotificationDataBlock> dataBlocks = new ArrayList<>();
}
