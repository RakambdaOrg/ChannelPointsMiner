package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("Selenium driver to use.")
public enum BrowserDriver{
	CHROME, FIREFOX, REMOTE_CHROME, REMOTE_FIREFOX
}
