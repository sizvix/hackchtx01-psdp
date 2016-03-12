package fr.hackchtx01.authentication.repository.inmemory;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import fr.hackchtx01.authentication.repository.OAuth2AccessTokenRepository;

/**
 * In memory implementation of the oauth2 access token repository
 * @deprecated very basic implementation
 * @author yoan
 */
@Deprecated
@Singleton
public class OAuth2AccessTokenInMemoryRepository extends OAuth2AccessTokenRepository {

	private final Map<String, UUID> userIdByAccessToken = Maps.newHashMap();
	
	@Override
	protected UUID processGetUserIdByAccessToken(String accessToken) {
		return userIdByAccessToken.get(accessToken);
	}

	@Override
	protected void processCreate(String accessToken, UUID userId) {
		userIdByAccessToken.put(accessToken, userId);
	}

	@Override
	protected void processDeleteByAccessToken(String accessToken) {
		userIdByAccessToken.remove(accessToken);
	}

}
