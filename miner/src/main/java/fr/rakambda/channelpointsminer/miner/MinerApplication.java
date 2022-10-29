package fr.rakambda.channelpointsminer.miner;

import fr.rakambda.channelpointsminer.miner.cli.CLIHolder;
import fr.rakambda.channelpointsminer.miner.cli.CLIParameters;
import fr.rakambda.channelpointsminer.miner.event.impl.MinerStartedEvent;
import fr.rakambda.channelpointsminer.miner.factory.ConfigurationFactory;
import fr.rakambda.channelpointsminer.miner.factory.MinerFactory;
import fr.rakambda.channelpointsminer.miner.factory.TimeFactory;
import fr.rakambda.channelpointsminer.miner.log.UnirestLogger;
import fr.rakambda.channelpointsminer.miner.util.GitProperties;
import fr.rakambda.channelpointsminer.miner.util.json.JacksonUtils;
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
public class MinerApplication{
	@SneakyThrows
	public static void main(String[] args){
		
		var version = GitProperties.getVersion();
		var commitId = GitProperties.getCommitId();
		var branch = GitProperties.getBranch();
		log.info("Starting everything up ({} | {} | {})", version, commitId, branch);
		
		CLIHolder.setInstance(parseCLIParameters(args));
		preSetup();
		
		var accountConfigurations = ConfigurationFactory.getInstance();
		log.info("Picked up configuration: {}", accountConfigurations);
		
		for(var accountConfiguration : accountConfigurations.getAccounts()){
			if(accountConfiguration.isEnabled()){
				var miner = MinerFactory.create(accountConfiguration);
				miner.start();
				miner.onEvent(new MinerStartedEvent(miner, version, commitId, branch, TimeFactory.now()));
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
				.setDefaultHeader(USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:104.0) Gecko/20100101 Firefox/104.0")
				.interceptor(new UnirestLogger());
	}
}
