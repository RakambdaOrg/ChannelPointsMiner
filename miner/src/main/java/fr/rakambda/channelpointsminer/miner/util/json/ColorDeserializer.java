package fr.rakambda.channelpointsminer.miner.util.json;

import lombok.extern.log4j.Log4j2;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.awt.*;

@Log4j2
public class ColorDeserializer extends StdDeserializer<Color>{
	public ColorDeserializer(){
		super(Color.class);
	}
	
	@Override
	public Color deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException{
		var value = p.getValueAsString();
		if(value.isBlank()){
			return null;
		}
		return Color.decode(value);
	}
}
