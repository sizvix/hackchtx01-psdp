package fr.hackchtx01.site.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

public enum SiteResourceErrorMessage implements ErrorMessage {
	/** Site not found */
	SITE_NOT_FOUND("Site not found"),
	/** Site Id is a mandatory field to update an site */
	MISSING_SITE_ID_FOR_UPDATE("Site Id is a mandatory field to update an site"),
	/** Site with Id : %s aready exists */
	ALREADY_EXISTING_SITE("Site with Id : %s already exists");

	private String message;
	
	private SiteResourceErrorMessage(String message) {
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