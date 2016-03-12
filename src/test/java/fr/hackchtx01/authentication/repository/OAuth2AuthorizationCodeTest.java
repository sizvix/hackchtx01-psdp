package fr.hackchtx01.authentication.repository;

import static org.fest.assertions.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCode;

public class OAuth2AuthorizationCodeTest {
	
	@Test(expected = NullPointerException.class)
	public void oAuth2AuthorizationCode_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new OAuth2AuthorizationCode(nullId, "code", LocalDateTime.now(), UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Auth code Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void oAuth2AuthorizationCode_should_fail_with_blank_code() {
		//given
		String blankCode = "  ";
		
		//when
		try {
			new OAuth2AuthorizationCode(UUID.randomUUID(), blankCode, LocalDateTime.now(), UUID.randomUUID());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid code");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void oAuth2AuthorizationCode_should_fail_without_user_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new OAuth2AuthorizationCode(UUID.randomUUID(), "code", LocalDateTime.now(), nullId);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("User ID is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void oAuth2AuthorizationCode_should_fail_without_creation_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new OAuth2AuthorizationCode(UUID.randomUUID(), "code", nullDate, UUID.randomUUID());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Creation date is mandatory");
			throw npe;
		}
	}
}