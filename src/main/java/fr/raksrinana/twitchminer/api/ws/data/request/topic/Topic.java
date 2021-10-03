package fr.raksrinana.twitchminer.api.ws.data.request.topic;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Builder;
import lombok.Getter;
import java.io.IOException;

@Getter
@Builder
@JsonSerialize(using = Topic.Serializer.class)
public class Topic{
	private TopicName name;
	private String target;
	
	static class Serializer extends StdSerializer<Topic>{
		public Serializer(){
			this(null);
		}
		
		protected Serializer(Class<Topic> t){
			super(t);
		}
		
		@Override
		public void serialize(Topic value, JsonGenerator gen, SerializerProvider provider) throws IOException{
			gen.writeString("%s.%s".formatted(
					value.getName().getValue(),
					value.getTarget()
			));
		}
	}
}
