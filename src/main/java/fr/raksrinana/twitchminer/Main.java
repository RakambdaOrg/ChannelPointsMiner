package fr.raksrinana.twitchminer;

import fr.raksrinana.twitchminer.api.passport.PassportApi;
import fr.raksrinana.twitchminer.api.ws.TwitchWebSocketPool;
import fr.raksrinana.twitchminer.cli.CLIHolder;
import fr.raksrinana.twitchminer.cli.CLIParameters;
import fr.raksrinana.twitchminer.factory.ConfigurationFactory;
import fr.raksrinana.twitchminer.factory.StreamerSettingsFactory;
import fr.raksrinana.twitchminer.miner.Miner;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import kong.unirest.*;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import static kong.unirest.HeaderNames.USER_AGENT;

@Log4j2
public class Main{
	@SneakyThrows
	public static void main(String[] args){
		CLIHolder.setInstance(parseCLIParameters(args));
		preSetup();
		
		var config = ConfigurationFactory.getInstance();
		
		var miner = new Miner(
				config,
				new PassportApi(config.getUsername(), config.getPassword(), config.getAuthenticationFolder(), config.isUse2Fa()),
				new StreamerSettingsFactory(config),
				new TwitchWebSocketPool(),
				Executors.newScheduledThreadPool(4),
				Executors.newCachedThreadPool());
		miner.start();
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
