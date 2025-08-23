package fr.rakambda.channelpointsminer.miner.api.hermes.data.response.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = PubSubNotificationType.class, name = "pubsub"),
})
@ToString
@EqualsAndHashCode
public abstract class NotificationData{
	@JsonProperty("subscription")
	private Subscription subscription;
	@JsonProperty("type")
	private String type;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	@Builder
	public static class Subscription{
		@JsonProperty("id")
		private String id;
	}
}
