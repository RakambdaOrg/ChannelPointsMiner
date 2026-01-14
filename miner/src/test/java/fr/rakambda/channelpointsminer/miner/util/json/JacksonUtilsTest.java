package fr.rakambda.channelpointsminer.miner.util.json;

import fr.rakambda.channelpointsminer.miner.tests.ParallelizableTest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tools.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ParallelizableTest
class JacksonUtilsTest{
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	static class TestObject{
		private String field1;
		private Integer field2;
	}
	
	@Test
	void readNominal() throws IOException{
		var content = """
				{
					"field1": "value1",
					"field2": 25
				}
				""";
		var expected = new TestObject("value1", 25);
		
		assertThat(JacksonUtils.read(content, new TypeReference<TestObject>(){})).isEqualTo(expected);
		
		try(var is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))){
			assertThat(JacksonUtils.read(is, new TypeReference<TestObject>(){})).isEqualTo(expected);
		}
	}
	
	@Test
	void readMissingField() throws IOException{
		var content = """
				{
					"field2": 25
				}
				""";
		var expected = new TestObject(null, 25);
		
		assertThat(JacksonUtils.read(content, new TypeReference<TestObject>(){})).isEqualTo(expected);
		
		try(var is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))){
			assertThat(JacksonUtils.read(is, new TypeReference<TestObject>(){})).isEqualTo(expected);
		}
	}
	
	@Test
	void readAllowTrailingComma() throws IOException{
		var content = """
				{
					"field1": "value1",
					"field2": 25,
				}
				""";
		var expected = new TestObject("value1", 25);
		
		assertThat(JacksonUtils.read(content, new TypeReference<TestObject>(){})).isEqualTo(expected);
		
		try(var is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))){
			assertThat(JacksonUtils.read(is, new TypeReference<TestObject>(){})).isEqualTo(expected);
		}
	}
	
	@Test
	void readUnmappedField() throws IOException{
		var content = """
				{
					"field3": 25
				}
				""";
		
		assertThatCode(() -> JacksonUtils.read(content, new TypeReference<TestObject>(){})).doesNotThrowAnyException();
		
		try(var is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))){
			assertThatCode(() -> JacksonUtils.read(is, new TypeReference<TestObject>(){})).doesNotThrowAnyException();
		}
	}
	
	@Test
	void writeNominal() throws IOException{
		var content = new TestObject("value1", 25);
		var expected = "{\"field1\":\"value1\",\"field2\":25}";
		
		assertThat(JacksonUtils.writeAsString(content)).isEqualTo(expected);
		
		try(var os = new ByteArrayOutputStream()){
			JacksonUtils.write(os, content);
			os.flush();
			var result = os.toString();
			assertThat(result).isEqualTo(expected);
		}
	}
	
	@Test
	void writeNullIgnored() throws IOException{
		var content = new TestObject(null, 25);
		var expected = "{\"field2\":25}";
		
		assertThat(JacksonUtils.writeAsString(content)).isEqualTo(expected);
		
		try(var os = new ByteArrayOutputStream()){
			JacksonUtils.write(os, content);
			os.flush();
			var result = os.toString();
			assertThat(result).isEqualTo(expected);
		}
	}
}