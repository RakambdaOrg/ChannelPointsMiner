package fr.raksrinana.twitchminer.cli;

import lombok.Getter;
import lombok.Setter;

public class CLIHolder{
	@Getter
	@Setter
	private static CLIParameters instance = new CLIParameters();
}
