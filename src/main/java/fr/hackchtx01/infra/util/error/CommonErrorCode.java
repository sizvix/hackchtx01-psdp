/**
 * 
 */
package fr.hackchtx01.infra.util.error;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Error code that can be used everywhere in the application
 * @author yoan
 */
public enum CommonErrorCode implements ErrorCode {
	APPLICATION_ERROR("APP-ERR"),
	API_RESPONSE("API-RESP");

	private String code;
	
	private CommonErrorCode(String code) {
		checkArgument(isNotBlank(code), "an error code should not be empty");
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}
}
