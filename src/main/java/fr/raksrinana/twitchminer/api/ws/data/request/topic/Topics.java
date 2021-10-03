package fr.raksrinana.twitchminer.api.ws.data.request.topic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

@Getter
@Builder
public class Topics{
	@JsonProperty("topics")
	@Singular
	private List<Topic> topics;
	@JsonProperty("auth_token")
	private String authToken;
	
	public static Topics buildFromName(@NotNull TopicName topicName, @NotNull String target, @Nullable String authToken){
		var topicBuilder = Topic.builder()
				.name(topicName)
				.target(target);
		
		var topicsBuilder = Topics.builder().topic(topicBuilder.build());
		if(topicName.isOwnUserTopic()){
			topicsBuilder = topicsBuilder.authToken(authToken);
		}
		return topicsBuilder.build();
	}
}
