package fr.hackchtx01.infra.util;

import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static fr.hackchtx01.test.TestHelper.assertWebApiException;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.infra.util.ResourceUtil;

public class ResourceUtilTest {
	
	@Test
	public void getIdfromParam_should_not_fail_with_null_paramName_and_valid_param() {
		//given
		UUID expectedUUID = UUID.randomUUID();
		String validParam = expectedUUID.toString();
		
		//when
		UUID result = ResourceUtil.getIdfromParam(null, validParam);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUUID);
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_clean_WebApiException_when_null_paramName_and_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getDevReadableMessage("Unknown param : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getIdfromParam(null, invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_clean_WebApiException_when_blank_paramName_and_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getDevReadableMessage("Unknown param : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getIdfromParam("  ", invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_null_param() {
		//given
		String paramName = "nullParam";
		String expectedMessage = INVALID.getDevReadableMessage("Param named " + paramName + " : " + null);
		
		//when
		try {
			ResourceUtil.getIdfromParam(paramName, null);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_blank_param() {
		//given
		String paramName = "blankParam";
		String expectedMessage = INVALID.getDevReadableMessage("Param named " + paramName + " :   ");
		
		//when
		try {
			ResourceUtil.getIdfromParam(paramName, "  ");
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getIdfromParam_should_fail_with_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getDevReadableMessage("Param named paramName : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getIdfromParam("paramName", invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getIdfromParam_should_work() {
		//given
		UUID expectedUUID = UUID.randomUUID();
		String validParam = expectedUUID.toString();
		
		//when
		UUID result = ResourceUtil.getIdfromParam("paramName", validParam);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUUID);
	}
	
	@Test
	public void getEmailfromParam_should_not_fail_with_null_paramName_and_valid_param() {
		//given
		String expectedEmail = "mail@mail.com";
		
		//when
		String result = ResourceUtil.getEmailfromParam(null, expectedEmail);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedEmail);
	}
	
	@Test(expected = WebApiException.class)
	public void getEmailfromParam_should_fail_with_clean_WebApiException_when_null_paramName_and_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getDevReadableMessage("Unknown param is not a valid email adress : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getEmailfromParam(null, invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getEmailfromParam_should_fail_with_clean_WebApiException_when_blank_paramName_and_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getDevReadableMessage("Unknown param is not a valid email adress : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getEmailfromParam("  ", invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getEmailfromParam_should_fail_with_null_param() {
		//given
		String paramName = "nullParam";
		String expectedMessage = INVALID.getDevReadableMessage("Param named " + paramName + " is not a valid email adress : " + null);
		
		//when
		try {
			ResourceUtil.getEmailfromParam(paramName, null);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getEmailfromParam_should_fail_with_blank_param() {
		//given
		String paramName = "blankParam";
		String expectedMessage = INVALID.getDevReadableMessage("Param named " + paramName + " is not a valid email adress :   ");
		
		//when
		try {
			ResourceUtil.getEmailfromParam(paramName, "  ");
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getEmailfromParam_should_fail_with_invalid_param() {
		//given
		String invalidParam = "invalid param";
		String expectedMessage = INVALID.getDevReadableMessage("Param named paramName is not a valid email adress : " + invalidParam);
		
		//when
		try {
			ResourceUtil.getEmailfromParam("paramName", invalidParam);
		} catch(WebApiException wae) {
		//then
			assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getEmailfromParam_should_work() {
		//given
		String expectedEmail = "mail@mail.com";
		
		//when
		String result = ResourceUtil.getEmailfromParam("paramName", expectedEmail);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedEmail);
	}
	
	@Test
	public void getEmailfromParam_should_return_email_inlower_case() {
		//given
		String emailWithUpperCase = "Mail@maIl.com";
		String expectedEmail = "mail@mail.com";
		
		//when
		String result = ResourceUtil.getEmailfromParam("paramName", emailWithUpperCase);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedEmail);
	}
}
