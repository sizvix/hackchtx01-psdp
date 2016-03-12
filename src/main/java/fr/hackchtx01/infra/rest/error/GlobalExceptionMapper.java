/**
 * 
 */
package fr.hackchtx01.infra.rest.error;

import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.error.ErrorCode;
import fr.hackchtx01.infra.util.error.RepositoryErrorCode;

/**
 * Map all exceptions from the application to an HTTP response
 * @author yoan
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
	
	/** associate each error code that does not go with status 500 to the specified HTTP status */
	private static final Map<ErrorCode, Status> CUSTOM_STATUS_BY_ERROR_CODE = ImmutableMap.<ErrorCode, Response.Status>builder()
			.put(APPLICATION_ERROR, INTERNAL_SERVER_ERROR)
			.put(RepositoryErrorCode.NOT_FOUND, NOT_FOUND)
			.build();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionMapper.class);
	
	/** The current HTTP request **/
	@Context
	private HttpServletRequest request;
	
	@Override
	public Response toResponse(Throwable exception) {
		ResponseBuilder apiResponse = null;
		
		if (exception instanceof WebApiException) {
			WebApiException webApiException = (WebApiException) exception;
			apiResponse = toResponse(webApiException);
		} else if (exception instanceof ApplicationException) {
			ApplicationException appException = (ApplicationException) exception;
			apiResponse = toResponse(appException);
		} else if (exception instanceof WebApplicationException) {
			//Exceptions Rest
			WebApplicationException webAppException = (WebApplicationException) exception;
			apiResponse = toResponse(webAppException);
		} else {
			apiResponse = defaultResponse(exception);
		}
		return apiResponse.header("Content-Type", getResponseMediaType()).build();
	}
	
	/**
	 * Convert a WebApiException to a response in the API format
	 * @param webApiException
	 * @return API formated response
	 */
	private ResponseBuilder toResponse(WebApiException webApiException) {
		String errorCode = webApiException.getErrorCode().getCode();
		if (!API_RESPONSE.equals(webApiException.getErrorCode())) {
			LOGGER.error("WebApiException : " + errorCode, webApiException);
		}
		Status status = webApiException.getStatus();
		ErrorRepresentation error = new ErrorRepresentation(webApiException.getLevel(), errorCode, webApiException.getMessage());
		return Response.status(status).entity(error);
	}
	
	/**
	 * Convert an ApplicationException to a response in the API format
	 * @param appException
	 * @return API formated response
	 */
	private ResponseBuilder toResponse(ApplicationException appException) {
		String errorCode = appException.getErrorCode().getCode();
		LOGGER.error("ApplicationException : " + errorCode, appException);
		Status status = getStatusFromErroCode(appException.getErrorCode());
		ErrorRepresentation error = new ErrorRepresentation(appException.getLevel(), errorCode, appException.getMessage());
		return Response.status(status).entity(error);
	}
	
	/**
	 * Convert a rest WebApplicationException to a response in the API format
	 * @param webAppException
	 * @return API formated response
	 */
	private ResponseBuilder toResponse(WebApplicationException webAppException) {
		int responseStatus = webAppException.getResponse().getStatus();
		if (responseStatus != NOT_FOUND.getStatusCode()) {
			LOGGER.error("WebApplicationException", webAppException);
		}
		ErrorRepresentation error = new ErrorRepresentation(Level.ERROR, getCodeFromHttpStatus(responseStatus), webAppException.getMessage());
		return Response.status(responseStatus).entity(error);
	}
	
	/**
	 * Convert an uncaught Exception to a response in the API format
	 * @param exception
	 * @return API formated response
	 */
	private ResponseBuilder defaultResponse(Throwable exception) {
		LOGGER.error("Uncaught error", exception);
		ErrorRepresentation error = new ErrorRepresentation(Level.ERROR, "UNKNOWN", exception.toString());
		return Response.status(INTERNAL_SERVER_ERROR).entity(error);
	}
	
	/**
	 * Get the media type to use for the reponse
	 * @return
	 */
	protected String getResponseMediaType() {
		String acceptHeader = request.getHeader("Accept");
		//if the Accept header is present
		if (StringUtils.isNotBlank(acceptHeader) && acceptHeader.contains("xml")) {
			//we return XML type if the header contains the word xml
			return APPLICATION_XML;
		}
		//by default we always return JSON type
		return APPLICATION_JSON;
	}
	
	private static String getCodeFromHttpStatus(int httpStatus) {
		return "HTTP " + httpStatus;
	}
	
	/**
	 * Get the HTTP status code associated to the error code
	 * @param code
	 * @return HTTP status
	 */
	private static Status getStatusFromErroCode(ErrorCode code) {
		if (CUSTOM_STATUS_BY_ERROR_CODE.containsKey(code)) {
			return CUSTOM_STATUS_BY_ERROR_CODE.get(code);
		}
		//by default we always return 500
		return INTERNAL_SERVER_ERROR;
	}
}
