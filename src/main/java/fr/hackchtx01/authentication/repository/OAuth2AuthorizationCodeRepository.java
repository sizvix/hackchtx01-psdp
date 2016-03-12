package fr.hackchtx01.authentication.repository;

import static fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.PROBLEM_INSERT_USER_ID_NULL;
import static fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.PROBLEM_INVALID_AUTH_CODE;
import static fr.hackchtx01.infra.logging.Markers.AUTHENTICATION;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * OAuth2 authorization code repository
 * @author yoan
 */
public abstract class OAuth2AuthorizationCodeRepository {
	
	public static final long AUTH_CODE_TTL_IN_MINUTES = 10;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthorizationCodeRepository.class);
	private static final Marker AUTH_MARKER = AUTHENTICATION.getMarker();
	
	public UUID getUserIdByAuthorizationCode(String authzCode) {
		if (StringUtils.isBlank(authzCode)) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INVALID_AUTH_CODE.getDevReadableMessage(authzCode, "searching user ID"));
			return null;
		}
		return processGetUserIdByAuthorizationCode(authzCode);
	};
	
	public void create(String authzCode, UUID userId) {
		if (StringUtils.isBlank(authzCode)) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INVALID_AUTH_CODE.getDevReadableMessage(authzCode, "inserting code for user : " + userId));
			return;
		}
		
		if (userId == null) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INSERT_USER_ID_NULL.getDevReadableMessage(authzCode));
			return;
		}
		
		processCreate(authzCode, userId);
	}
	
	public void deleteByCode(String authzCode) {
		if (StringUtils.isBlank(authzCode)) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INVALID_AUTH_CODE.getDevReadableMessage(authzCode, "deleting auth code"));
			return;
		}
		
		processDeleteByCode(authzCode);
	}
	
	protected abstract UUID processGetUserIdByAuthorizationCode(String authzCode);
	
	protected abstract void processCreate(String authzCode, UUID userId);
	
	protected abstract void processDeleteByCode(String authzCode);
}
