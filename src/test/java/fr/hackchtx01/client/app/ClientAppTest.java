package fr.hackchtx01.client.app;

import static org.fest.assertions.api.Assertions.assertThat;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.test.TestHelper;

public class ClientAppTest {
	
	@Test(expected = NullPointerException.class)
	public void clientApp_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new ClientApp(nullId, "name", UUID.randomUUID(), TestHelper.TEST_URI, LocalDateTime.now(), LocalDateTime.now(), "Secret", UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("App Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void clientApp_should_fail_with_blank_name() {
		//given
		String blankName = "  ";
		
		//when
		try {
			new ClientApp(UUID.randomUUID(), blankName, UUID.randomUUID(), TestHelper.TEST_URI, LocalDateTime.now(), LocalDateTime.now(), "Secret", UUID.randomUUID());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid app name");
			throw iae;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void clientApp_should_fail_with_blank_secret() {
		//given
		String blankSecret = "  ";
		
		//when
		try {
			new ClientApp(UUID.randomUUID(), "name", UUID.randomUUID(), TestHelper.TEST_URI, LocalDateTime.now(), LocalDateTime.now(), blankSecret, UUID.randomUUID());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid app secret");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void clientApp_should_fail_without_Salt() {
		//given
		UUID nullSalt = null;
		
		//when
		try {
			new ClientApp(UUID.randomUUID(), "name", UUID.randomUUID(), TestHelper.TEST_URI, LocalDateTime.now(), LocalDateTime.now(), "Secret", nullSalt);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("The app secret hash salt is mandatory");
			throw npe;
		}
	} 
	
	@Test(expected = NullPointerException.class)
	public void clientApp_should_fail_without_owner_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new ClientApp(UUID.randomUUID(), "name", nullId, TestHelper.TEST_URI, LocalDateTime.now(), LocalDateTime.now(), "Secret", UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("App owner Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void clientApp_should_fail_without_creation_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new ClientApp(UUID.randomUUID(), "name", UUID.randomUUID(), TestHelper.TEST_URI, nullDate, LocalDateTime.now(), "Secret", UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Creation date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void clientApp_should_fail_without_last_update_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new ClientApp(UUID.randomUUID(), "name", UUID.randomUUID(), TestHelper.TEST_URI, LocalDateTime.now(), nullDate, "Secret", UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Last update date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void clientApp_should_fail_without_redirect_URI() {
		//given
		URI nullRedirectURI = null;
		
		//when
		try {
			new ClientApp(UUID.randomUUID(), "name", UUID.randomUUID(), nullRedirectURI, LocalDateTime.now(), LocalDateTime.now(), "Secret", UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Oauth2 redirect URI is mandatory");
			throw npe;
		}
	}
}