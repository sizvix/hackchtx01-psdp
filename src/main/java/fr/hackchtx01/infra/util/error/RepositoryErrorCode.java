/**
 * 
 */
package fr.hackchtx01.infra.util.error;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Error codes specific to the repositories
 * @author yoan
 */
public enum RepositoryErrorCode implements ErrorCode {
	ALREADY_EXISTING("ALREADY_EXISTING"),
	NOT_FOUND("NOT_FOUND");

	private String code;
	
	private RepositoryErrorCode(String code) {
		checkArgument(isNotBlank(code), "an error code should not be empty");
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}
}