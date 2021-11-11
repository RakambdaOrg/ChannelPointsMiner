package fr.raksrinana.channelpointsminer;

import fr.raksrinana.channelpointsminer.cli.CLIHolder;
import fr.raksrinana.channelpointsminer.cli.CLIParameters;
import fr.raksrinana.channelpointsminer.factory.ConfigurationFactory;
import fr.raksrinana.channelpointsminer.factory.MinerFactory;
import fr.raksrinana.channelpointsminer.util.json.JacksonUtils;
import kong.unirest.*;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;
import static kong.unirest.HeaderNames.USER_AGENT;

@Log4j2
public class Main{
	@SneakyThrows
	public static void main(String[] args){
		// #############################
		// Fix for JDK 17 : https://bugs.openjdk.java.net/browse/JDK-8274349?focusedCommentId=14450437&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-14450437
		int cores = Runtime.getRuntime().availableProcessors();
		if(cores <= 1){
			log.warn("Available Cores \"" + cores + "\", setting Parallelism Flag");
			System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
		}
		// #############################
		
		CLIHolder.setInstance(parseCLIParameters(args));
		preSetup();
		
		var config = ConfigurationFactory.getInstance();
		
		MinerFactory.create(config).start();
	}
	
	@NotNull
	private static CLIParameters parseCLIParameters(@NotNull String[] args){
		var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(CommandLine.ParameterException e){
			log.error("Failed to parse arguments", e);
			cli.usage(System.out);
			throw new IllegalStateException("Failed to load environment");
		}
		
		return parameters;
	}
	
	private static void preSetup(){
		Unirest.config()
				.enableCookieManagement(true)
				.setObjectMapper(new JacksonObjectMapper(JacksonUtils.getMapper()))
				.setDefaultHeader(USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:85.0) Gecko/20100101 Firefox/85.0")
				.interceptor(new Interceptor(){
					@Override
					public void onRequest(HttpRequest<?> request, Config config){
						log.trace("Sending request to {}", request.getUrl());
					}
					
					@Override
					public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config){
						if(!response.isSuccess()){
							log.error("Failed to request {} got statusCode `{}` and parsing error: {}", request.getUrl(), response.getStatus(), response.getParsingError());
							response.getParsingError().ifPresent(ex -> log.error("Failed to parse body: {}", ex.getOriginalBody()));
						}
						else{
							log.trace("Received successful response for {} with statusCode `{}`", request.getUrl(), response.getStatus());
						}
					}
				});
	}
}
