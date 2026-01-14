package fr.rakambda.channelpointsminer.miner.api.pubsub.data.request.topic;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;
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
			super(Topic.class);
		}
		
		@Override
		public void serialize(Topic value, JsonGenerator gen, SerializationContext provider) throws JacksonException{
			gen.writeString(value.getValue());
		}
	}
	
	static class Deserializer extends StdDeserializer<Topic>{
		protected Deserializer(){
			super(Topic.class);
		}
		
		@Override
		public Topic deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
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
