package fr.raksrinana.twitchminer.api.kraken.data.follows;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.util.json.ColorDeserializer;
import fr.raksrinana.twitchminer.util.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.twitchminer.util.json.URLDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.net.URL;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Channel{
	@JsonProperty("mature")
	private boolean mature;
	@JsonProperty("status")
	@Nullable
	private String status;
	@JsonProperty("broadcaster_language")
	@NotNull
	private String broadcasterLanguage;
	@JsonProperty("broadcaster_software")
	@NotNull
	private String broadcasterSoftware;
	@JsonProperty("display_name")
	@NotNull
	private String displayName;
	@JsonProperty("game")
	@Nullable
	private String game;
	@JsonProperty("language")
	@NotNull
	private String language;
	@JsonProperty("_id")
	@NotNull
	private String id;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime createdAt;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime updatedAt;
	@JsonProperty("partner")
	private boolean partner;
	@JsonProperty("logo")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL logo;
	@JsonProperty("video_banner")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL videoBanner;
	@JsonProperty("profile_banner")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL profileBanner;
	@JsonProperty("profile_banner_background_color")
	@JsonDeserialize(using = ColorDeserializer.class)
	@Nullable
	private Color profileBannerBackgroundColor;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	@NotNull
	private URL url;
	@JsonProperty("views")
	private long views;
	@JsonProperty("followers")
	private long followers;
	@JsonProperty("broadcaster_type")
	@NotNull
	private String broadcasterType;
	@JsonProperty("description")
	@Nullable
	private String description;
	@JsonProperty("private_video")
	private boolean privateVideo;
	@JsonProperty("privacy_options_enabled")
	private boolean privacyOptionsEnabled;
}
