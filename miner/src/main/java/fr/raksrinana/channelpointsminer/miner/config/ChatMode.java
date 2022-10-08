package fr.raksrinana.channelpointsminer.miner.config;

import com.fasterxml.jackson.annotation.JsonClassDescription;

@JsonClassDescription("Way of joining Twitch's chat.")
public enum ChatMode{
	WS,
	IRC
}
