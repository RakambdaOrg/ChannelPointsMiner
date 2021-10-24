package fr.raksrinana.twitchminer.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.time.ZonedDateTime;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class ISO8601ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime>{
	public ISO8601ZonedDateTimeDeserializer(){
		this(null);
	}
	
	protected ISO8601ZonedDateTimeDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	@Nullable
	public ZonedDateTime deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext context) throws IOException{
		var value = jsonParser.getValueAsString();
		if(value.isBlank()){
			return null;
		}
		return ZonedDateTime.parse(value, ISO_DATE_TIME);
	}
}
