package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("Way to get current Twitch's version.")
public enum VersionProvider{
	MANIFEST,
	WEBPAGE
}
