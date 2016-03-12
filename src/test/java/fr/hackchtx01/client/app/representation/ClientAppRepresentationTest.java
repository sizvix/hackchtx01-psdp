package fr.hackchtx01.client.app.representation;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.google.common.collect.Lists;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.representation.ClientAppRepresentation;
import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.test.TestHelper;

public class ClientAppRepresentationTest {
	
	@Test(expected = NullPointerException.class)
	public void clientAppRepresentation_should_fail_without_clientApp() {
		//given
		ClientApp nullClientApp = null;
		
		//when
		new ClientAppRepresentation(nullClientApp, mock(UriInfo.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void clientAppRepresentation_should_fail_without_UriInfo() {
		//given
		UriInfo nullUriInfo = null;
		
		//when
		new ClientAppRepresentation(TestHelper.generateRandomClientApp(), nullUriInfo);
	}
	
	@Test
	public void clientAppRepresentation_should_contains_clientApp_self_link() {
		//given
		ClientApp clientApp = TestHelper.generateRandomClientApp();
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		
		//when
		ClientAppRepresentation result = new ClientAppRepresentation(clientApp, mockedUriInfo);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(clientApp.getId());
		assertThat(result.getLinks()).isNotNull();
		assertThat(result.getLinks()).isNotEmpty();
		assertThat(result.getLinks()).contains(Link.self(expectedURL));
	}
	
	@Test(expected = NullPointerException.class)
	public void toClientApp_should_fail_without_representation() {
		//given
		ClientAppRepresentation nullRepresentation = null;
		
		//when
		try {
			ClientAppRepresentation.toClientApp(nullRepresentation);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create client application from null ClientAppRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toClientApp_should_fail_with_invalid_Representation() {
		//given
		@SuppressWarnings("deprecation")
		ClientAppRepresentation invalidClientAppRepresentation = new ClientAppRepresentation(UUID.randomUUID(), " ", UUID.randomUUID(), "http://test", " ", Lists.newArrayList());
		String expectedMessage = INVALID.getDevReadableMessage("client application") + " : Invalid app name";
		
		//when
		try {
			ClientAppRepresentation.toClientApp(invalidClientAppRepresentation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toClientApp_should_fail_with_invalid_redirect_URI() {
		//given
		@SuppressWarnings("deprecation")
		ClientAppRepresentation invalidClientAppRepresentation = new ClientAppRepresentation(UUID.randomUUID(), " ", UUID.randomUUID(), " ", " ", Lists.newArrayList());
		String expectedMessage = INVALID.getDevReadableMessage("client application");
		
		//when
		try {
			ClientAppRepresentation.toClientApp(invalidClientAppRepresentation);
		} catch(WebApiException wae) {
		//then
			assertThat(wae.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(wae.getLevel()).isEqualTo(ERROR);
			assertThat(wae.getErrorCode()).isEqualTo(API_RESPONSE);
			assertThat(wae.getMessage()).startsWith(expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void toClientApp_should_work() {
		//given
		ClientApp expectedClientApp = TestHelper.generateRandomClientApp();
		@SuppressWarnings("deprecation")
		ClientAppRepresentation validClientAppRepresentation = new ClientAppRepresentation(expectedClientApp.getId(), expectedClientApp.getName(), expectedClientApp.getOwnerId(), expectedClientApp.getRedirectURI().toString(), expectedClientApp.getSecret(), Lists.newArrayList());
		
		//when
		ClientApp result = ClientAppRepresentation.toClientApp(validClientAppRepresentation);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(expectedClientApp.getName());
		assertThat(result.getOwnerId()).isEqualTo(expectedClientApp.getOwnerId());
		assertThat(result.getRedirectURI()).isEqualTo(expectedClientApp.getRedirectURI());
	}
}
