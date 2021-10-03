package fr.raksrinana.twitchminer.api.kraken.data.follows;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.twitchminer.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.twitchminer.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class Channel{
	@JsonProperty("mature")
	private boolean mature;
	@JsonProperty("status")
	private String status;
	@JsonProperty("broadcaster_language")
	private String broadcasterLanguage;
	@JsonProperty("broadcaster_software")
	private String broadcasterSoftware;
	@JsonProperty("display_name")
	private String displayName;
	@JsonProperty("game")
	private String game;
	@JsonProperty("language")
	private String language;
	@JsonProperty("_id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("partner")
	private boolean partner;
	@JsonProperty("logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logo;
	@JsonProperty("video_banner")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL videoBanner;
	@JsonProperty("profile_banner")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL profileBanner;
	@JsonProperty("profile_banner_background_color")
	private String profileBannerBackgroundColor;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
	@JsonProperty("views")
	private long views;
	@JsonProperty("followers")
	private long followers;
	@JsonProperty("broadcaster_type")
	private String broadcasterType;
	@JsonProperty("description")
	private String description;
	@JsonProperty("private_video")
	private boolean privateVideo;
	@JsonProperty("privacy_options_enabled")
	private boolean privacyOptionsEnabled;
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Channel channel = (Channel) o;
		return id == channel.id;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
}
