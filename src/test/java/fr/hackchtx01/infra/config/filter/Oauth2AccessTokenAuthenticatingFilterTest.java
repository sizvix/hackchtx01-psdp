package fr.hackchtx01.infra.config.filter;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mock;

import fr.hackchtx01.infra.config.filter.Oauth2AccessTokenAuthenticatingFilter;

public class Oauth2AccessTokenAuthenticatingFilterTest {
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	
	@Test
	public void extractAccessToken_should_work_with_valid_OAuth2_Authorization_header() {
		//given
		String expectedAccessToken = "2d685f0f8b076a2eb161ca6da9ad250e";
		String validHeader = "Bearer " + expectedAccessToken;
		
		//when
		String result = Oauth2AccessTokenAuthenticatingFilter.extractAccessToken(validHeader);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedAccessToken);
	}
	
	@Test
	public void extractAccessToken_should_return_null_with_blank_access_token() {
		//given
		String expectedAccessToken = " ";
		String validHeader = "Bearer " + expectedAccessToken;
		
		//when
		String result = Oauth2AccessTokenAuthenticatingFilter.extractAccessToken(validHeader);
		
		//then
		assertThat(result).isNull();
	}
}
