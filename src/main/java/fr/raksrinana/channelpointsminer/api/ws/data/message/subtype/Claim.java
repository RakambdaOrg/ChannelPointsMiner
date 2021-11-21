package fr.raksrinana.channelpointsminer.api.ws.data.message.subtype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.channelpointsminer.util.json.ISO8601ZonedDateTimeDeserializer;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Claim{
	@JsonProperty("id")
	@NotNull
	private String id;
	@JsonProperty("user_id")
	@NotNull
	private String userId;
	@JsonProperty("channel_id")
	@NotNull
	private String channelId;
	@JsonProperty("point_gain")
	@NotNull
	private PointGain pointGain;
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime createdAt;
}
