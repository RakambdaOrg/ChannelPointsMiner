package fr.rakambda.channelpointsminer.miner.api.ws.data.message.watchpartyvod;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.util.json.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import java.net.URL;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class Vod {
	@JsonProperty("wp_id")
	@Nullable
	private String wpId;
	@JsonProperty("wp_type")
	@Nullable
	private String wpType;
    @JsonProperty("increment_url")
    @JsonDeserialize(using = URLDeserializer.class)
    @Nullable
    private URL incrementUrl;
    @JsonProperty("vod_id")
	@Nullable
	private String vodId;
    @JsonProperty("title")
	@Nullable
	private String title;
    @JsonProperty("broadcast_type")
	@Nullable
	private String broadcastType;
    @JsonProperty("viewable")
	@Nullable
	private String viewable;
}
