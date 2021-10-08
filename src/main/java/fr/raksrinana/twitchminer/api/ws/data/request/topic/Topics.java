package fr.raksrinana.twitchminer.api.ws.data.request.topic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import static java.util.stream.Collector.Characteristics.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Topics{
	@JsonProperty("topics")
	private Set<Topic> topics = new HashSet<>();
	@JsonProperty("auth_token")
	@ToString.Exclude
	private String authToken;
	
	public static Topics buildFromName(@NotNull TopicName topicName, @NotNull String target, @Nullable String authToken){
		var topicBuilder = Topic.builder()
				.name(topicName)
				.target(target);
		
		var topicsBuilder = Topics.builder().topics(Set.of(topicBuilder.build()));
		if(topicName.isOwnUserTopic()){
			topicsBuilder = topicsBuilder.authToken(authToken);
		}
		return topicsBuilder.build();
	}
	
	public int getTopicCount(){
		return topics.size();
	}
	
	public Topics merge(@NotNull Topics topics){
		if(Objects.isNull(getAuthToken()) && Objects.nonNull(topics.getAuthToken())){
			authToken = topics.getAuthToken();
		}
		this.topics.addAll(topics.getTopics());
		return this;
	}
	
	public static class TopicsCollector implements Collector<Topics, Topics, Topics>{
		@Override
		public Supplier<Topics> supplier(){
			return Topics::new;
		}
		
		@Override
		public BiConsumer<Topics, Topics> accumulator(){
			return Topics::merge;
		}
		
		@Override
		public BinaryOperator<Topics> combiner(){
			return Topics::merge;
		}
		
		@Override
		public Function<Topics, Topics> finisher(){
			return t -> t;
		}
		
		@Override
		public Set<Characteristics> characteristics(){
			return Set.of(UNORDERED, CONCURRENT, IDENTITY_FINISH);
		}
	}
}
