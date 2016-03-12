package fr.hackchtx01.authentication.repository;

import static org.fest.assertions.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import fr.hackchtx01.authentication.repository.OAuth2AccessToken;

public class OAuth2AccessTokenTest {
	
	@Test(expected = NullPointerException.class)
	public void oAuth2AccessToken_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new OAuth2AccessToken(nullId, "token", LocalDateTime.now(), UUID.randomUUID(), 0);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Access token Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void oAuth2AccessToken_should_fail_with_blank_token() {
		//given
		String blankToken = "  ";
		
		//when
		try {
			new OAuth2AccessToken(UUID.randomUUID(), blankToken, LocalDateTime.now(), UUID.randomUUID(), 0);
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid token");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void oAuth2AccessToken_should_fail_without_user_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new OAuth2AccessToken(UUID.randomUUID(), "token", LocalDateTime.now(), nullId, 0);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("User ID is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void oAuth2AccessToken_should_fail_without_creation_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new OAuth2AccessToken(UUID.randomUUID(), "token", nullDate, UUID.randomUUID(), 0);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Creation date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void oAuth2AccessToken_should_fail_with_negative_number_of_refresh() {
		//given
		int negativeNbRefresh = -1;
		
		//when
		try {
			new OAuth2AccessToken(UUID.randomUUID(), "token", LocalDateTime.now(), UUID.randomUUID(), negativeNbRefresh);
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid number of refresh");
			throw iae;
		}
	}
}
