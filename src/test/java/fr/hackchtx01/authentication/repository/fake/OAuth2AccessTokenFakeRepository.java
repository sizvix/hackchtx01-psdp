package fr.hackchtx01.authentication.repository.fake;

import java.util.UUID;

import fr.hackchtx01.authentication.repository.OAuth2AccessTokenRepository;

/**
 * Fake implementation of OAuth2 access token repository
 * Test purpose only
 * @author yoan
 */
public class OAuth2AccessTokenFakeRepository extends OAuth2AccessTokenRepository {
	@Override
	protected UUID processGetUserIdByAccessToken(String accessToken) {
		return null;
	}

	@Override
	protected void processCreate(String accessToken, UUID userId) { }

	@Override
	protected void processDeleteByAccessToken(String accessToken) { }
}