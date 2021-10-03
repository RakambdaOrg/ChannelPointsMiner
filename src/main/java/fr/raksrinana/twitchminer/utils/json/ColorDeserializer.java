package fr.raksrinana.twitchminer.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.io.IOException;

@Log4j2
public class ColorDeserializer extends StdDeserializer<Color>{
	public ColorDeserializer(){
		this(null);
	}
	
	protected ColorDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	@Nullable
	public Color deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		return Color.decode(jsonParser.getValueAsString());
	}
}
