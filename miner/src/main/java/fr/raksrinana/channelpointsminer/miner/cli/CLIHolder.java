package fr.raksrinana.channelpointsminer.miner.cli;

import lombok.Getter;
import lombok.Setter;

public class CLIHolder{
	@Getter
	@Setter
	private static CLIParameters instance = new CLIParameters();
}
