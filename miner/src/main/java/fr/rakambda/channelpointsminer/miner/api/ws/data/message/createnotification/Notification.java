package fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.channelpointsminer.miner.api.ws.data.message.subtype.NotificationDisplayType;
import fr.rakambda.channelpointsminer.miner.util.json.ISO8601ZonedDateTimeDeserializer;
import fr.rakambda.channelpointsminer.miner.util.json.URLDeserializer;
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
	@JsonProperty("type")
	@NotNull
	private String type;
}
