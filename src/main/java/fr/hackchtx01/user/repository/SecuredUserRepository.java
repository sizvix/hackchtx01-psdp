/**
 * 
 */
package fr.hackchtx01.user.repository;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static fr.hackchtx01.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static fr.hackchtx01.user.repository.UserRepositoryErrorMessage.PROBLEM_PASSWORD_VALIDITY;
import static fr.hackchtx01.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.helper.SecurityHelper;
import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.User;

/**
 * User repository focused on security information
 * @author yoan
 */
public abstract class SecuredUserRepository {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SecuredUserRepository.class);
	
	/**
	 * Create a new User
	 * @param userToCreate
	 */
	public void create(User askedUserToCreate, String password) {
		if (askedUserToCreate == null) {
			LOGGER.warn("Secured user creation asked with null user");
			return;
		}
		ensurePasswordValidity(password);
		
		User userToCreate = forceCreationDate(askedUserToCreate);
		SecuredUser securedUserToCreate = generateSecuredUser(userToCreate, password);
		processCreate(securedUserToCreate);
	}
	
	private User forceCreationDate(User user) {
		LocalDateTime creationDate = LocalDateTime.now();
		return User.Builder.createFrom(user)
			.withCreationDate(creationDate)
			.withLastUpdate(creationDate)
			.build();
	}
	
	private SecuredUser generateSecuredUser(User basicUserInfos, String password) {
		Object salt = generateSalt();
		String hashedPassword = hashPassword(password, salt);
		return SecuredUser.Builder.createFrom(basicUserInfos)
								   .withPassword(hashedPassword)
								   .withSalt(salt)
								   .build();
	}
	
	protected Object generateSalt() {
		return UUID.randomUUID().toString();
	}
	
	protected void ensurePasswordValidity(String password) {
		if (!checkPasswordValidity(password)) {
			String message = PROBLEM_PASSWORD_VALIDITY.getDevReadableMessage();
			LOGGER.error(message);
			throw new ApplicationException(ERROR, UNSECURE_PASSWORD, message);
		}
	}
	
	protected boolean checkPasswordValidity(String password) {
		return StringUtils.isNotBlank(password);
	}
	
	public String hashPassword(String password, Object salt) {
		return SecurityHelper.hash(password, salt);
	}
	
	/**
	 * Get a user by its email adress
	 * @param userEmail
	 * @return found user with security infos
	 */
	public final SecuredUser getByEmail(String userEmail) {
		if (StringUtils.isBlank(userEmail)) {
			return null;
		}
		return processGetByEmail(userEmail);
	}
	
	/**
	 * Get a user by its Id
	 * @param userId
	 * @return found user with security infos
	 */
	public final SecuredUser getById(UUID userId) {
		if (userId == null) {
			return null;
		}
		return processGetById(userId);
	}
	
	/**
	 * Get a user by its Id and fail if it does not exist
	 * @param userId
	 * @return found user
	 * @throws ApplicationException if user not found
	 */
	public final User findUser(UUID userId) {
		SecuredUser foundUser = getById(userId);
		
		if (foundUser == null) {
			throw new ApplicationException(INFO, NOT_FOUND, USER_NOT_FOUND);
		}
		
		return foundUser;
	}
	
	/**
	 * Change user password
	 * @param userId
	 * @param newPassword
	 */
	public final void changePassword(UUID userId, String newPassword) {
		if (userId == null) {
			LOGGER.warn("Password update asked with null user");
			return;
		}
		ensurePasswordValidity(newPassword);
		
		User existingUser = findUser(userId);
		existingUser = forceLastUpdateDate(existingUser);
		SecuredUser securedUserToUpdate = generateSecuredUser(existingUser, newPassword);
		processChangePassword(securedUserToUpdate);
	}
	
	private User forceLastUpdateDate(User user) {
		return User.Builder.createFrom(user)
			.withLastUpdate(LocalDateTime.now())
			.build();
	}
	
	/**
	 * Create a new SecuredUser
	 * @param userToCreate
	 */
	protected abstract void processCreate(SecuredUser userToCreate);
	
	/**
	 * Get a user by its Id
	 * @param userId
	 */
	protected abstract SecuredUser processGetById(UUID userId);
	
	/**
	 * Get a user by its email adress
	 * @param userEmail
	 */
	protected abstract SecuredUser processGetByEmail(String userEmail);
	
	/**
	 * Update password
	 * @param userToUpdate
	 */
	protected abstract void processChangePassword(SecuredUser userToUpdate);
}
