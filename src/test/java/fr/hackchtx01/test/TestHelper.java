/**
 * 
 */
package fr.hackchtx01.test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

import fr.hackchtx01.authentication.repository.OAuth2AccessToken;
import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCode;
import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.infra.rest.error.ErrorRepresentation;
import fr.hackchtx01.infra.rest.error.Level;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.error.ErrorCode;
import fr.hackchtx01.infra.util.error.ErrorMessage;
import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.User;

/**
 * Unit test helper
 * @author yoan
 */
public class TestHelper {
	private TestHelper() { }
	
	public static URI TEST_URI = URI.create("http://localhost:8080");
	
	private static Random rand = new Random();
	private static final ImmutableList<String> SYLLABS = ImmutableList.<String>of("yo", "an", "ad", "ri", "en", "e", "mi", "li", "en");
	
	public static void assertApplicationException(ApplicationException ae, Level expectedLevel, ErrorCode expectedErrorCode, String expectedMessage) {
		assertThat(ae.getLevel()).isEqualTo(expectedLevel);
		assertThat(ae.getErrorCode()).isEqualTo(expectedErrorCode);
		assertThat(ae.getMessage()).as("message").isEqualTo(expectedMessage);
	}
	
	public static void assertApplicationException(ApplicationException ae, Level expectedLevel, ErrorCode expectedErrorCode, ErrorMessage expectedMessage) {
		assertApplicationException(ae, expectedLevel, expectedErrorCode, expectedMessage.getDevReadableMessage());
	}
	
	public static void assertWebApiException(WebApiException wae, Status expectedStatus, Level expectedLevel, ErrorCode expectedErrorCode, String expectedMessage) {
		assertThat(wae.getStatus()).isEqualTo(expectedStatus);
		assertApplicationException(wae, expectedLevel, expectedErrorCode, expectedMessage);
	}
	
	public static void assertWebApiException(WebApiException wae, Status expectedStatus, Level expectedLevel, ErrorCode expectedErrorCode, ErrorMessage expectedMessage) {
		assertWebApiException(wae, expectedStatus, expectedLevel, expectedErrorCode, expectedMessage.getDevReadableMessage());
	}
	
	public static void assertErrorResponse(Response errorResponse, Status expectedStatus, Level expectedLevel, String expectedErrorCode, String expectedMessage) {
		assertThat(errorResponse).isNotNull();
		assertThat(errorResponse.getStatus()).isEqualTo(expectedStatus.getStatusCode());
		ErrorRepresentation payload = (ErrorRepresentation) errorResponse.getEntity();
		assertThat(payload).isNotNull();
		assertThat(payload.getLevel()).isEqualTo(expectedLevel);
		assertThat(payload.getCode()).isEqualTo(expectedErrorCode);
		assertThat(payload.getMessage()).isEqualTo(expectedMessage);
	}
	
	/**
	 * Create UriInfo mock for Resource and link creation purpose
	 * @param expectedURL : expected URL
	 * @return
	 */
	public static UriInfo mockUriInfo(String expectedURL) {
		UriBuilder uriBuilder = mock(UriBuilder.class);
		when(uriBuilder.path((String) anyVararg())).thenReturn(uriBuilder);
		when(uriBuilder.path(any(Class.class))).thenReturn(uriBuilder);
		when(uriBuilder.path(any(Class.class), anyString())).thenReturn(uriBuilder);
		when(uriBuilder.build()).thenReturn(URI.create(expectedURL));
		when(uriBuilder.build(anyVararg())).thenReturn(URI.create(expectedURL));
		when(uriBuilder.build(anyVararg(), Mockito.eq(false))).thenReturn(URI.create(expectedURL));
		UriInfo mockedUriInfo = mock(UriInfo.class);
		when(mockedUriInfo.getAbsolutePath()).thenReturn(URI.create(expectedURL));
		when(mockedUriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
		when(mockedUriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
		
		return mockedUriInfo;
	}
	
	public static User generateRandomUser() {
		return User.Builder.createDefault()
						   .withRandomId()
						   .withName(generateRandomName())
						   .build();
	}
	
	public static SecuredUser generateRandomSecuredUser() {
		User user = User.Builder.createDefault().withRandomId().build();
		return SecuredUser.Builder.createFrom(user)
								  .withSalt(UUID.randomUUID().toString())
								  .withRawPassword(UUID.randomUUID().toString())
								  .build();
	}
	
	public static ClientApp generateRandomClientApp() {
		return ClientApp.Builder.createDefault()
								   .withRandomId()
								   .withName(generateRandomName())
								   .withOwnerId(UUID.randomUUID())
								   .withSecret(UUID.randomUUID().toString())
								   .withSalt(UUID.randomUUID().toString())
								   .build();
	}
	
	public static OAuth2AuthorizationCode generateRandomOAuth2AuthorizationCode() {
		return OAuth2AuthorizationCode.Builder.createDefault()
											   .withRandomId()
											   .withCode(generateString(5))
											   .withUserId(UUID.randomUUID())
											   .build();
	}
	
	public static OAuth2AccessToken generateRandomOAuth2AccessToken() {
		return OAuth2AccessToken.Builder.createDefault()
											   .withRandomId()
											   .withToken(generateString(5))
											   .withUserId(UUID.randomUUID())
											   .withNbRefresh(generateRandomInt(0, 10))
											   .build();
	}
	
	public static String generateRandomName() {
		return generateString(generateRandomInt(1, 3));
	}
	
	public static int generateRandomInt(int min, int max) {
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	public static String generateString(int length) {
	    StringBuilder name = new StringBuilder();
	    for (int i = 0; i < length; i++) {
	    	name.append(SYLLABS.get(rand.nextInt(SYLLABS.size())));
	    }
	    return name.toString();
	}
}
