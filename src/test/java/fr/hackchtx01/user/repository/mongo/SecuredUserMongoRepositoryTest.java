package fr.hackchtx01.user.repository.mongo;

import static fr.hackchtx01.infra.db.Dbs.PSDP;
import static fr.hackchtx01.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static fr.hackchtx01.user.repository.mongo.UserMongoRepository.USER_COLLECTION;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.test.fongo.FongoBackedTest;
import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.mongo.SecuredUserMongoConverter;
import fr.hackchtx01.user.repository.mongo.SecuredUserMongoRepository;
import fr.hackchtx01.user.repository.mongo.UserMongoConverter;

public class SecuredUserMongoRepositoryTest extends FongoBackedTest {
	
	private final SecuredUserMongoConverter converter = new SecuredUserMongoConverter();
	private final MongoCollection<Document> userCollection = getFongoDbConnectionFactory().getCollection(PSDP, USER_COLLECTION);
	
	@InjectMocks
	SecuredUserMongoRepository testedRepo;
	
	@Test
	public void create_should_work() {
		//given
		SecuredUser expectedUser = TestHelper.generateRandomSecuredUser();
		String expectedPassword = expectedUser.getPassword();

		//when
		testedRepo.create(expectedUser, expectedPassword);
		
		//then
		Bson filter = Filters.eq("_id", expectedUser.getId());
		Document result = userCollection.find().filter(filter).first();
		SecuredUser securedUser = converter.fromDocument(result);
		User user = User.Builder.createFrom(securedUser).build();
		
		assertThat(user).isEqualTo(User.Builder.createFrom(expectedUser).build());
		//with the generated salt we should be able to generate the same hash
		String hash = testedRepo.hashPassword(expectedPassword, securedUser.getSalt());
		assertThat(securedUser.getPassword()).isEqualTo(hash);
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_already_existing_user_and_same_password() {
		//given
		User alreadyExistingUser = TestHelper.generateRandomUser();
		testedRepo.create(alreadyExistingUser, "password");
		
		//when
		try {
			testedRepo.create(alreadyExistingUser, "password");
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_USER.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing user still exists
			Bson filter = Filters.eq("_id", alreadyExistingUser.getId());
			Document result = userCollection.find().filter(filter).first();
			User user = new UserMongoConverter().fromDocument(result);
			assertThat(user).isEqualTo(alreadyExistingUser);
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_already_existing_user_and_different_password() {
		//given
		User alreadyExistingUser = TestHelper.generateRandomUser();
		testedRepo.create(alreadyExistingUser, "password1");
		SecuredUser alreadyExistingSecuredUser = testedRepo.getById(alreadyExistingUser.getId());
		
		//when
		try {
			testedRepo.create(alreadyExistingUser, "password2");
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_USER.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing user still exists
			Bson filter = Filters.eq("_id", alreadyExistingUser.getId());
			Document result = userCollection.find().filter(filter).first();
			SecuredUser securedUser = converter.fromDocument(result);
			assertThat(securedUser).isEqualTo(alreadyExistingSecuredUser);
		}
	}
	
	@Test
	public void getById_should_return_null_with_not_existing_user_id() {
		//given
		UUID notExistingUserId = UUID.randomUUID();

		//when
		User result = testedRepo.getById(notExistingUserId);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getById_should_work_with_existing_user_id() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		String originalPassword = "password";
		testedRepo.create(expectedUser, originalPassword);
		

		//when
		SecuredUser result = testedRepo.getById(expectedUser.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(User.Builder.createFrom(result).build()).isEqualTo(expectedUser);
		//with the generated salt we should be able to generate the same hash
		String hash = testedRepo.hashPassword(originalPassword, result.getSalt());
		assertThat(result.getPassword()).isEqualTo(hash);
	}
	
	@Test
	public void changePassword_should_work_with_existing_user() throws InterruptedException {
		//given
		User originalUser = TestHelper.generateRandomUser();
		String originalPassword = "originalPW";
		testedRepo.create(originalUser, originalPassword);
		SecuredUser originalSecuredUser = testedRepo.getById(originalUser.getId());
		String newPassword = "newPW";
		Thread.sleep(1);

		//when
		testedRepo.changePassword(originalUser.getId(), newPassword);
		
		//then
		SecuredUser result = testedRepo.getById(originalUser.getId());
		assertThat(result).isNotNull();
		assertThat(User.Builder.createFrom(result).build()).isEqualTo(User.Builder.createFrom(originalSecuredUser).build());
		//creation date should not change
		assertThat(result.getCreationDate()).isEqualTo(originalSecuredUser.getCreationDate());
		//last update date should have changed
		assertThat(result.getLastUpdate().isAfter(originalSecuredUser.getLastUpdate())).isTrue();
	}
	
	@Test
	public void getByEmail_should_return_null_with_not_existing_user_email() {
		//given
		String notExistingUserEmail = "not_existing@mail.com";

		//when
		User result = testedRepo.getByEmail(notExistingUserEmail);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getByEmail_should_work_with_existing_user_id() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		String originalPassword = "password";
		testedRepo.create(expectedUser, originalPassword);
		

		//when
		SecuredUser result = testedRepo.getByEmail(expectedUser.getEmail());
		
		//then
		assertThat(result).isNotNull();
		assertThat(User.Builder.createFrom(result).build()).isEqualTo(expectedUser);
		//with the generated salt we should be able to generate the same hash
		String hash = testedRepo.hashPassword(originalPassword, result.getSalt());
		assertThat(result.getPassword()).isEqualTo(hash);
	}
}
