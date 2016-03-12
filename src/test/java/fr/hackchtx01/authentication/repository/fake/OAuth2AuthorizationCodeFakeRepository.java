package fr.hackchtx01.authentication.repository.fake;

import java.util.UUID;

import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepository;

/**
 * Fake implementation of OAuth2 authorization code repository
 * Test purpose only
 * @author yoan
 */
public class OAuth2AuthorizationCodeFakeRepository extends OAuth2AuthorizationCodeRepository {
	@Override
	protected UUID processGetUserIdByAuthorizationCode(String authzCode) {
		return null;
	}

	@Override
	protected void processCreate(String authzCode, UUID userId) { }

	@Override
	protected void processDeleteByCode(String authzCode) { }
}