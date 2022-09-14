package fr.raksrinana.channelpointsminer.miner.tests;

import kong.unirest.core.Expectation;
import kong.unirest.core.HttpMethod;
import kong.unirest.core.MockClient;

public class UnirestMock{
	private MockClient unirest;
	
	public UnirestMock(){
		reset();
	}
	
	public void reset(){
		unirest = MockClient.register();
	}
	
	public void verifyAll(){
		unirest.verifyAll();
	}
	
	public Expectation expect(HttpMethod method, String path){
		return unirest.expect(method, path);
	}
}
