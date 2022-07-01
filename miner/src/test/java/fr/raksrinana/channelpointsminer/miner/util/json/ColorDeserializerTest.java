package fr.raksrinana.channelpointsminer.miner.util.json;

import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.channelpointsminer.miner.tests.ParallelizableTest;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import static org.assertj.core.api.Assertions.assertThat;

@ParallelizableTest
class ColorDeserializerTest extends DeserializerTest<Color>{
	@Test
	void hexValue(){
		assertThat(deserialize("\"%s\"".formatted("#FF0000"))).isEqualTo(Color.RED);
	}
	
	@Test
	void decValue(){
		assertThat(deserialize("%d".formatted(0xFF0000))).isEqualTo(Color.RED);
	}
	
	@Test
	void empty(){
		assertThat(deserialize("\"\"")).isNull();
	}
	
	@Override
	protected JsonDeserializer<Color> getDeserializer(){
		return new ColorDeserializer();
	}
}