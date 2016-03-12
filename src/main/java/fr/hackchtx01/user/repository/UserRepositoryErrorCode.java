package fr.hackchtx01.user.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorCode;

/**
 * Error codes specific to the user repositories
 * @author yoan
 */
public enum UserRepositoryErrorCode implements ErrorCode {
	UNSECURE_PASSWORD("UNSECURE_PASSWORD");

	private String code;
	
	private UserRepositoryErrorCode(String code) {
		checkArgument(isNotBlank(code), "an error code should not be empty");
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}
}