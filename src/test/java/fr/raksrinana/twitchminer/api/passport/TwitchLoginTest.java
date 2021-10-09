package fr.raksrinana.twitchminer.api.passport;

import fr.raksrinana.twitchminer.api.gql.GQLApi;
import fr.raksrinana.twitchminer.api.gql.data.GQLResponse;
import fr.raksrinana.twitchminer.api.gql.data.reportmenuitem.ReportMenuItemData;
import fr.raksrinana.twitchminer.api.gql.data.types.User;
import fr.raksrinana.twitchminer.factory.ApiFactory;
import kong.unirest.Cookie;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitchLoginTest{
	private static final String USER_ID = "user-id";
	private static final String USERNAME = "username";
	
	@Mock
	private GQLApi gqlApi;
	@Mock
	private GQLResponse<ReportMenuItemData> gqlResponse;
	@Mock
	private ReportMenuItemData reportMenuItemData;
	@Mock
	private User user;
	
	@BeforeEach
	void setUp(){
		lenient().when(gqlApi.reportMenuItem(USERNAME)).thenReturn(Optional.of(gqlResponse));
		lenient().when(gqlResponse.getData()).thenReturn(reportMenuItemData);
		lenient().when(reportMenuItemData.getUser()).thenReturn(user);
		lenient().when(user.getId()).thenReturn(USER_ID);
	}
	
	@Test
	void getUserIdFromCookies(){
		var tested = TwitchLogin.builder()
				.username(USERNAME)
				.cookies(List.of(new Cookie("persistent=%s%%3A%%3Aabcdefghijklmnopqrstuvwxyz".formatted(USER_ID))))
				.build();
		
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class)){
			assertThat(tested.getUserId()).isEqualTo(USER_ID);
			
			apiFactory.verifyNoInteractions();
		}
	}
	
	@Test
	void getUserIdFromApi(){
		var tested = TwitchLogin.builder()
				.username(USERNAME)
				.cookies(List.of())
				.build();
		
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.getGqlApi(tested)).thenReturn(gqlApi);
			
			assertThat(tested.getUserId()).isEqualTo(USER_ID);
		}
	}
	
	@Test
	void getUserIdSavesResult(){
		var tested = TwitchLogin.builder()
				.username(USERNAME)
				.cookies(List.of())
				.build();
		
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.getGqlApi(tested)).thenReturn(gqlApi);
			
			assertThat(tested.getUserId()).isEqualTo(USER_ID);
			
			apiFactory.verify(() -> ApiFactory.getGqlApi(any()));
		}
	}
	
	@Test
	void getUserIdFromApiNoResponse(){
		var tested = TwitchLogin.builder()
				.username(USERNAME)
				.cookies(List.of())
				.build();
		
		try(var apiFactory = Mockito.mockStatic(ApiFactory.class)){
			apiFactory.when(() -> ApiFactory.getGqlApi(tested)).thenReturn(gqlApi);
			
			when(gqlApi.reportMenuItem(USERNAME)).thenReturn(Optional.empty());
			
			assertThrows(IllegalStateException.class, () -> tested.getUserId());
		}
	}
}