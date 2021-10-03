package fr.raksrinana.twitchminer.api;

import lombok.extern.log4j.Log4j2;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Log4j2
public class Constants{
	public static URL TWITCH_URL;
	
	static {
		try{
			TWITCH_URL = URI.create("https://www.twitch.tv").toURL();
		}
		catch(MalformedURLException e){
			log.error("Failed to construct constants", e);
		}
	}
}
