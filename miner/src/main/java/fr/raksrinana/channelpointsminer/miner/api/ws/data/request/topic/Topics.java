package fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
public class Topics{
	@JsonProperty("topics")
	private Set<Topic> topics;
	@JsonProperty("auth_token")
	@ToString.Exclude
	private String authToken;
	
	public Topics(@NotNull Topic topic){
		this(topic, null);
	}
	
	public Topics(@NotNull Topic topic, @Nullable String authToken){
		topics = Set.of(topic);
		this.authToken = authToken;
	}
	
	public static Topics buildFromName(@NotNull TopicName topicName, @NotNull String target, @Nullable String authToken){
		var topic = Topic.builder()
				.name(topicName)
				.target(target)
				.build();
		
		return new Topics(topic, authToken);
	}
	
	public int getTopicCount(){
		return topics.size();
	}
}
