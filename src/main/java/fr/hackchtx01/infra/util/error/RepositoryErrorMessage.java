package fr.hackchtx01.infra.util.error;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Error messages specific to the repositories
 * @author yoan
 */
public enum RepositoryErrorMessage implements ErrorMessage {
	/** The document does not contain an _id */
	MONGO_DOCUMENT_WITHOUT_ID("The document does not contain an _id");
	
	private String message;
	
	private RepositoryErrorMessage(String message) {
		checkArgument(isNotBlank(message), "An error message should not be empty");
		this.message = message;
	}
	
	@Override
	public String getDevReadableMessage() {
		return message;
	}

	@Override
	public String getDevReadableMessage(Object... params) {
		return String.format(message, params);
	}
}
