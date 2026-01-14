package fr.rakambda.channelpointsminer.miner.api.discord.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.StdSerializer;
import static java.lang.Math.min;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Field{
	public static final int MAX_VALUE_SIZE = 1024;
	
	@JsonProperty("name")
	private String name;
	@JsonProperty("value")
	@JsonSerialize(using = ValueSerializer.class)
	private String value;
	@JsonProperty("inline")
	@Builder.Default
	private boolean inline = true;
	
	private static class ValueSerializer extends StdSerializer<String>{
		public ValueSerializer(){
			this(null);
		}
		
		protected ValueSerializer(Class<String> t){
			super(t);
		}
		
		@Override
		public void serialize(String value, JsonGenerator gen, SerializationContext provider) throws JacksonException{
			gen.writeString(value.substring(0, min(value.length(), MAX_VALUE_SIZE)));
		}
	}
}
