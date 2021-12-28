package fr.raksrinana.channelpointsminer.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.net.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Raid{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("creator_id")
	@NotNull
	private String creatorId;
	@JsonProperty("source_id")
	@NotNull
	private String sourceId;
	@JsonProperty("target_id")
	@NotNull
	private String targetId;
	@JsonProperty("target_login")
	@NotNull
	private String targetLogin;
	@JsonProperty("target_display_name")
	@NotNull
	private String targetDisplayName;
	@JsonProperty("target_profile_image")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL targetProfileImage;
	@JsonProperty("transition_jitter_seconds")
	private int transitionJitterSeconds;
	@JsonProperty("force_raid_now_seconds")
	private int forceRaidNowSeconds;
	@JsonProperty("viewer_count")
	private int viewerCount;
}
