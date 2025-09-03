package fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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
	
	public Topics(@NonNull Topic topic){
		this(topic, null);
	}
	
	public Topics(@NonNull Topic topic, @Nullable String authToken){
		topics = Set.of(topic);
		this.authToken = authToken;
	}
	
	public static Topics buildFromName(@NonNull TopicName topicName, @NonNull String target, @Nullable String authToken){
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
