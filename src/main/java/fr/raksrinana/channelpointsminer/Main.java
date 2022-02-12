package fr.raksrinana.channelpointsminer;

import fr.raksrinana.channelpointsminer.cli.CLIHolder;
import fr.raksrinana.channelpointsminer.cli.CLIParameters;
import fr.raksrinana.channelpointsminer.factory.ConfigurationFactory;
import fr.raksrinana.channelpointsminer.factory.MinerFactory;
import fr.raksrinana.channelpointsminer.log.UnirestLogger;
import fr.raksrinana.channelpointsminer.log.event.MinerStartedLogEvent;
import fr.raksrinana.channelpointsminer.util.GitProperties;
import fr.raksrinana.channelpointsminer.util.json.JacksonUtils;
import kong.unirest.core.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;
import static kong.unirest.core.HeaderNames.USER_AGENT;

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
		
		var version = GitProperties.getVersion();
		var commitId = GitProperties.getCommitId();
		var branch = GitProperties.getBranch();
		log.info("Starting everything up ({} | {} | {})", version, commitId, branch);
		
		CLIHolder.setInstance(parseCLIParameters(args));
		preSetup();
		
		var accountConfigurations = ConfigurationFactory.getInstance();
		for(var accountConfiguration : accountConfigurations.getAccounts()){
			if(accountConfiguration.isEnabled()){
				var miner = MinerFactory.create(accountConfiguration);
				miner.start();
				miner.onLogEvent(new MinerStartedLogEvent(miner, version, commitId, branch));
			}
			else{
				log.info("Account {} is disabled, skipping it", accountConfiguration.getUsername());
			}
		}
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
				.interceptor(new UnirestLogger());
	}
}
