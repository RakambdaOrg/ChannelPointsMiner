package fr.raksrinana.channelpointsminer.miner.api.ws.data.message.createnotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("DATA_BLOCK_TYPE_TEXT")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class TextNotificationDataBlock extends NotificationDataBlock{
	@JsonProperty("content")
	@Nullable
	private TextContent content;
}
