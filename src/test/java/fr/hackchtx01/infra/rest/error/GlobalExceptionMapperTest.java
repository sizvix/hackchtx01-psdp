package fr.hackchtx01.infra.rest.error;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.hackchtx01.infra.rest.error.GlobalExceptionMapper;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.error.RepositoryErrorCode;
import fr.hackchtx01.test.TestHelper;

@RunWith(MockitoJUnitRunner.class)
public class GlobalExceptionMapperTest {
	
	@Mock
	HttpServletRequest mockedRequest;
	
	@InjectMocks
	GlobalExceptionMapper tested;
	
	private static class UnknownException extends Exception {
		private static final long serialVersionUID = 6062338122666716525L;
		
		public UnknownException() {
			super();
		}
	}
	
	@Test
	public void getResponseMediaType_should_return_APPLICATION_XML_if_Accept_header_contains_xml() {
		//given
		when(mockedRequest.getHeader("Accept")).thenReturn(APPLICATION_XML);
		
		//when
		String result = tested.getResponseMediaType();
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(APPLICATION_XML);
	}
	
	@Test
	public void getResponseMediaType_should_return_APPLICATION_JSON_if_Accept_header_does_not_contain_xml() {
		//given
		when(mockedRequest.getHeader("Accept")).thenReturn("something");
		
		//when
		String result = tested.getResponseMediaType();
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(APPLICATION_JSON);
	}
	
	@Test
	public void toResponse_should_return_unknown_error_with_unknown_exception() {
		//given
		UnknownException unknownException = new UnknownException();
		String expectedMessage = unknownException.toString();
		
		//when
		Response response = tested.toResponse(unknownException);
		
		//then
		TestHelper.assertErrorResponse(response, INTERNAL_SERVER_ERROR, ERROR, "UNKNOWN", expectedMessage);
	}
	
	@Test
	public void toResponse_should_handle_WebApplicationException() {
		//given
		String expectedMessage = "expected message";
		Status expectedStatus = NOT_FOUND;
		String expectedCode = "HTTP " + expectedStatus.getStatusCode();
		WebApplicationException webApplException = new WebApplicationException(expectedMessage, expectedStatus);
		
		//when
		Response response = tested.toResponse(webApplException);
		
		//then
		TestHelper.assertErrorResponse(response, expectedStatus, ERROR, expectedCode, expectedMessage);
	}
	
	@Test
	public void toResponse_should_handle_ApplicationException() {
		//given
		String expectedMessage = "expected message";
		ApplicationException applException = new ApplicationException(ERROR, APPLICATION_ERROR, expectedMessage);
		
		//when
		Response response = tested.toResponse(applException);
		
		//then
		TestHelper.assertErrorResponse(response, INTERNAL_SERVER_ERROR, ERROR, APPLICATION_ERROR.getCode(), expectedMessage);
	}
	
	@Test
	public void toResponse_should_handle_ApplicationException_with_NOT_FOUND_repository_error_code() {
		//given
		String expectedMessage = "expected message";
		ApplicationException applException = new ApplicationException(ERROR, RepositoryErrorCode.NOT_FOUND, expectedMessage);
		
		//when
		Response response = tested.toResponse(applException);
		
		//then
		TestHelper.assertErrorResponse(response, NOT_FOUND, ERROR, RepositoryErrorCode.NOT_FOUND.getCode(), expectedMessage);
	}
	
	@Test
	public void toResponse_should_handle_WebApiException() {
		//given
		String expectedMessage = "expected message";
		WebApiException applException = new WebApiException(NOT_FOUND, ERROR, APPLICATION_ERROR, expectedMessage);
		
		//when
		Response response = tested.toResponse(applException);
		
		//then
		TestHelper.assertErrorResponse(response, NOT_FOUND, ERROR, APPLICATION_ERROR.getCode(), expectedMessage);
	}
}
