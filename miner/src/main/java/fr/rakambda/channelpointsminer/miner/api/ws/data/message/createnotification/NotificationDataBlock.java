package fr.rakambda.channelpointsminer.miner.api.ws.data.message.createnotification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = TextNotificationDataBlock.class, name = "DATA_BLOCK_TYPE_TEXT"),
		@JsonSubTypes.Type(value = ImageNotificationDataBlock.class, name = "DATA_BLOCK_TYPE_IMAGE"),
})
@EqualsAndHashCode
@ToString
@SuperBuilder
public class NotificationDataBlock{
	@JsonProperty("id")
	@NotNull
	private String id;
}
