package fr.hackchtx01.client.app.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorCode;

/**
 * Error codes specific to the client app repository
 * @author yoan
 */
public enum ClientAppRepositoryErrorCode implements ErrorCode {
	UNSECURE_SECRET("UNSECURE_SECRET");
	
	private String code;
	
	private ClientAppRepositoryErrorCode(String code) {
		checkArgument(isNotBlank(code), "an error code should not be empty");
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}
}
