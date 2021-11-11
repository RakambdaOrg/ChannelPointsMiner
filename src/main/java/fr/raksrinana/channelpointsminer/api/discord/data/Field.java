package fr.raksrinana.channelpointsminer.api.discord.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.*;
import java.io.IOException;
import static java.lang.Math.min;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
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
		public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException{
			gen.writeString(value.substring(0, min(value.length(), MAX_VALUE_SIZE)));
		}
	}
}
