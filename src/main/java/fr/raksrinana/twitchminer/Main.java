package fr.raksrinana.twitchminer;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.kraken.KrakenApi;
import fr.raksrinana.twitchminer.api.passport.PassportApi;
import fr.raksrinana.twitchminer.api.passport.TwitchLogin;
import fr.raksrinana.twitchminer.api.passport.exceptions.CaptchaSolveRequired;
import fr.raksrinana.twitchminer.cli.CLIParameters;
import fr.raksrinana.twitchminer.config.Configuration;
import fr.raksrinana.twitchminer.miner.Miner;
import fr.raksrinana.twitchminer.miner.Streamer;
import fr.raksrinana.twitchminer.miner.StreamerSettingsFactory;
import fr.raksrinana.twitchminer.utils.json.JacksonUtils;
import kong.unirest.*;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;
import static kong.unirest.HeaderNames.USER_AGENT;

@Log4j2
public class Main{
	@Getter
	private static CLIParameters parameters;
	@Getter
	private static TwitchLogin twitchLogin;
	
	@SneakyThrows
	public static void main(String[] args){
		parameters = loadEnv(args);
		preSetup();
		
		var config = Configuration.getInstance();
		try{
			twitchLogin = new PassportApi(config.getUsername(), config.getPassword(), config.getAuthenticationFolder(), config.isUse2Fa()).login();
		}
		catch(CaptchaSolveRequired e){
			log.error("A captcha solve is required, please log in through your browser and solve it");
		}
		catch(Exception e){
			log.error("Failed to login", e);
			return;
		}
		
		var miner = new Miner();
		miner.start();
		
		config.getStreamers().stream()
				.map(streamer -> {
					var user = GQLApi.reportMenuItem(streamer.getUsername())
							.map(GQLResponse::getData)
							.map(ReportMenuItemData::getUser)
							.orElseThrow(() -> new RuntimeException("Failed to get streamer id for " + streamer.getUsername()));
					return new Streamer(user.getId(), streamer.getUsername(), StreamerSettingsFactory.readStreamerSettings());
				})
				.forEach(miner::addStreamer);
		
		if(config.isLoadFollows()){
			log.info("Loading streamers from follow list");
			KrakenApi.getFollows().stream()
					.filter(follow -> !miner.hasStreamerWithUsername(follow.getChannel().getName()))
					.map(follow -> new Streamer(follow.getChannel().getId(), follow.getChannel().getName(), StreamerSettingsFactory.readStreamerSettings()))
					.forEach(miner::addStreamer);
		}
	}
	
	@NotNull
	private static CLIParameters loadEnv(@NotNull String[] args){
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
						}
						else{
							log.trace("Received successful response for {} with statusCode `{}`", request.getUrl(), response.getStatus());
						}
					}
				});
	}
}
