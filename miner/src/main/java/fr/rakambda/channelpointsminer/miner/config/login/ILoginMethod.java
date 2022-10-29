package fr.rakambda.channelpointsminer.miner.config.login;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = HttpLoginMethod.class, name = "http"),
		@JsonSubTypes.Type(value = BrowserConfiguration.class, name = "browser"),
		@JsonSubTypes.Type(value = MobileLoginMethod.class, name = "mobile"),
})
@JsonClassDescription("Login method")
public interface ILoginMethod{
}
