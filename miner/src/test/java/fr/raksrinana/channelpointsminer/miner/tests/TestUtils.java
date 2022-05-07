package fr.raksrinana.channelpointsminer.miner.tests;

import lombok.SneakyThrows;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestUtils{
	private static final String LF = "\n";
	
	public static String getAllContent(Path path){
		return getAllContent(path, LF);
	}
	
	public static String getAllContent(Path path, String lineTerminator){
		return String.join(lineTerminator, getContent(path));
	}
	
	@SneakyThrows
	public static List<String> getContent(Path path){
		return Files.readAllLines(path);
	}
	
	public static String getAllResourceContent(String resource){
		return getAllResourceContent(resource, LF);
	}
	
	public static String getAllResourceContent(String resource, String lineTerminator){
		return String.join(lineTerminator, getResourceContent(resource));
	}
	
	public static List<String> getResourceContent(String resource){
		return getContent(getResourcePath(resource));
	}
	
	@SneakyThrows
	public static Path getResourcePath(String resource){
		return Paths.get(TestUtils.class.getClassLoader().getResource(resource).toURI());
	}
	
	@SneakyThrows
	public static Path copyFromResources(String resource, Path target){
		return Files.copy(getResourcePath(resource), target);
	}
}
