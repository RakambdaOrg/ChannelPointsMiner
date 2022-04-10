package fr.raksrinana.channelpointsminer.miner.api.ws.data.request.topic;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.io.IOException;
import java.util.Optional;

@Getter
@Builder
@JsonSerialize(using = Topic.Serializer.class)
@JsonDeserialize(using = Topic.Deserializer.class)
@EqualsAndHashCode
@ToString
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
			gen.writeString(value.getValue());
		}
	}
	
	static class Deserializer extends StdDeserializer<Topic>{
		protected Deserializer(){
			this(null);
		}
		
		protected Deserializer(Class<?> vc){
			super(vc);
		}
		
		@Override
		public Topic deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
			return Optional.ofNullable(p.getValueAsString())
					.map(t -> t.split("\\."))
					.flatMap(t -> TopicName.fromValue(t[0]).map(name -> new Topic(name, t[1])))
					.orElse(null);
		}
	}
	
	public String getValue(){
		return "%s.%s".formatted(
				getName().getValue(),
				getTarget()
		);
	}
}
