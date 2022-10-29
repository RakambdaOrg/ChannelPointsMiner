package fr.rakambda.channelpointsminer.miner.api.passport;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TwitchClient{
	MOBILE("kd1unb4b3q4t58fwlpcbzcbnm76a8fp"),
	WEB("kimne78kx3ncx6brgo4mv6wki5h1ko");
	
	private final String clientId;
}
