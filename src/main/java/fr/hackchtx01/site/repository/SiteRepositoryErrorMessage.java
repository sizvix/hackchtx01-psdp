package fr.hackchtx01.site.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

public enum SiteRepositoryErrorMessage implements ErrorMessage {
	/** Error while reading site : %s */
	PROBLEM_READ_SITE("Error while reading site : %s"),
	/** Error while creating site : %s */
	PROBLEM_CREATION_SITE("Error while creating site : %s"),
	/** Error while updating site : %s */
	PROBLEM_UPDATE_SITE("Error while updating site : %s"),
	/** Error while deleting site : %s */
	PROBLEM_DELETE_SITE("Error while deleting site : %s"),
	/** Error while creating site URL : %s */
	PROBLEM_CREATION_SITE_URL("Error while creating site URL : %s");
	
	private String message;
	
	private SiteRepositoryErrorMessage(String message) {
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