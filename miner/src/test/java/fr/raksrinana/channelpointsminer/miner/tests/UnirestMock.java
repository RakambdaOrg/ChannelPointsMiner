package fr.raksrinana.channelpointsminer.miner.tests;

import kong.unirest.core.Expectation;
import kong.unirest.core.HttpMethod;
import kong.unirest.core.MockClient;
import kong.unirest.core.UnirestInstance;
import lombok.Getter;

public class UnirestMock{
	@Getter
	private final UnirestInstance unirestInstance;
	private MockClient unirest;
	
	public UnirestMock(UnirestInstance unirestInstance){
		this.unirestInstance = unirestInstance;
		reset();
	}
	
	public void reset(){
		unirest = MockClient.register(unirestInstance);
	}
	
	public void verifyAll(){
		unirest.verifyAll();
	}
	
	public Expectation expect(HttpMethod method, String path){
		return unirest.expect(method, path);
	}
}
