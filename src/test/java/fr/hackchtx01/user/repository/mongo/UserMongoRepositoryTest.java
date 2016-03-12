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
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.mongo.UserMongoConverter;
import fr.hackchtx01.user.repository.mongo.UserMongoRepository;

public class UserMongoRepositoryTest extends FongoBackedTest {
	
	private final UserMongoConverter converter = new UserMongoConverter();
	private final MongoCollection<Document> userCollection = getFongoDbConnectionFactory().getCollection(PSDP, USER_COLLECTION);
	
	@InjectMocks
	UserMongoRepository testedRepo;
	
	@Test
	public void create_should_work() {
		//given
		User expectedUser = TestHelper.generateRandomUser();

		//when
		testedRepo.create(expectedUser);
		
		//then
		Bson filter = Filters.eq("_id", expectedUser.getId());
		Document result = userCollection.find().filter(filter).first();
		User user = converter.fromDocument(result);
		assertThat(user).isEqualTo(expectedUser);
		assertThat(user.getCreationDate()).isEqualTo(user.getLastUpdate());
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_message_with_already_existing_user() {
		//given
		User alreadyExistingUser = TestHelper.generateRandomUser();
		testedRepo.create(alreadyExistingUser);
		
		//when
		try {
			testedRepo.create(alreadyExistingUser);
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_USER.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing user still exists
			Bson filter = Filters.eq("_id", alreadyExistingUser.getId());
			Document result = userCollection.find().filter(filter).first();
			User user = converter.fromDocument(result);
			assertThat(user).isEqualTo(alreadyExistingUser);
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
		testedRepo.create(expectedUser);

		//when
		User result = testedRepo.getById(expectedUser.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
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
	public void getByEmail_should_work_with_existing_user_email() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		testedRepo.create(expectedUser);

		//when
		User result = testedRepo.getByEmail(expectedUser.getEmail());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
	}
	
	@Test
	public void update_should_work_with_existing_user() throws InterruptedException {
		//given
		User originalUser = TestHelper.generateRandomUser();
		testedRepo.create(originalUser);
		originalUser = testedRepo.getById(originalUser.getId());
		String modifiedName = "new " + originalUser.getName();
		User modifiedUser = User.Builder.createFrom(originalUser).withName(modifiedName).build();
		Thread.sleep(1);
		
		//when
		testedRepo.update(modifiedUser);
		
		//then
		User result = testedRepo.getById(originalUser.getId());
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(modifiedName);
		assertThat(result).isEqualTo(modifiedUser);
		//creation date should not change
		assertThat(result.getCreationDate()).isEqualTo(originalUser.getCreationDate());
		//last update date should have changed
		assertThat(result.getLastUpdate().isAfter(originalUser.getLastUpdate())).isTrue();
	}
	
	@Test
	public void deleteById_should_not_fail_with_not_existing_user_id() {
		//given
		UUID notExistingUserId = UUID.randomUUID();

		//when
		testedRepo.deleteById(notExistingUserId);
		
		//then
		//should not have failed
	}
	
	@Test
	public void deleteById_should_work_with_existing_user_id() {
		//given
		User existingUser = TestHelper.generateRandomUser();
		testedRepo.create(existingUser);

		//when
		testedRepo.deleteById(existingUser.getId());
		
		//then
		User result = testedRepo.getById(existingUser.getId());
		assertThat(result).isNull();
	}
}
