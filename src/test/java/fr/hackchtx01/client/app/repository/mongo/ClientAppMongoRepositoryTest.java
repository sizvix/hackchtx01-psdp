package fr.hackchtx01.client.app.repository.mongo;

import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_CREATION_CLIENT_APP;
import static fr.hackchtx01.client.app.repository.mongo.ClientAppMongoRepository.CLIENT_APP_COLLECTION;
import static fr.hackchtx01.infra.db.Dbs.PSDP;
import static fr.hackchtx01.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static org.fest.assertions.api.Assertions.assertThat;

import java.net.URI;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.mongo.ClientAppMongoConverter;
import fr.hackchtx01.client.app.repository.mongo.ClientAppMongoRepository;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.test.fongo.FongoBackedTest;

public class ClientAppMongoRepositoryTest extends FongoBackedTest {
	
	private final ClientAppMongoConverter converter = new ClientAppMongoConverter();
	private final MongoCollection<Document> clientAppCollection = getFongoDbConnectionFactory().getCollection(PSDP, CLIENT_APP_COLLECTION);
	
	@InjectMocks
	ClientAppMongoRepository testedRepo;
	
	@Test
	public void create_should_work() {
		//given
		ClientApp expectedClientApp = TestHelper.generateRandomClientApp();
		String expectedSecret = expectedClientApp.getSecret();

		//when
		testedRepo.create(expectedClientApp, expectedSecret);
		
		//then
		Bson filter = Filters.eq(FIELD_ID, expectedClientApp.getId());
		Document result = clientAppCollection.find().filter(filter).first();
		ClientApp clientApp = converter.fromDocument(result);
		
		Object salt = clientApp.getSalt();
		String hash = testedRepo.hashSecret(expectedSecret, salt);
		expectedClientApp = ClientApp.Builder.createFrom(expectedClientApp).withSecret(hash).withSalt(salt).build();
		assertThat(clientApp).isEqualTo(expectedClientApp);
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_already_existing_app_and_same_secret() {
		//given
		ClientApp alreadyExistingClientApp = TestHelper.generateRandomClientApp();
		testedRepo.create(alreadyExistingClientApp, "secret");
		alreadyExistingClientApp = testedRepo.getById(alreadyExistingClientApp.getId());
		
		//when
		try {
			testedRepo.create(alreadyExistingClientApp, "secret");
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_CLIENT_APP.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing client app still exists
			Bson filter = Filters.eq(FIELD_ID, alreadyExistingClientApp.getId());
			Document result = clientAppCollection.find().filter(filter).first();
			ClientApp clientApp = converter.fromDocument(result);
			assertThat(clientApp).isEqualTo(alreadyExistingClientApp);
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_already_existing_user_and_different_password() {
		//given
		ClientApp alreadyExistingClientApp = TestHelper.generateRandomClientApp();
		testedRepo.create(alreadyExistingClientApp, "secret1");
		alreadyExistingClientApp = testedRepo.getById(alreadyExistingClientApp.getId());
		
		//when
		try {
			testedRepo.create(alreadyExistingClientApp, "secret2");
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_CLIENT_APP.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing user still exists
			Bson filter = Filters.eq(FIELD_ID, alreadyExistingClientApp.getId());
			Document result = clientAppCollection.find().filter(filter).first();
			ClientApp clientApp = converter.fromDocument(result);
			assertThat(clientApp).isEqualTo(alreadyExistingClientApp);
		}
	}
	
	@Test
	public void getById_should_return_null_with_not_existing_client_id() {
		//given
		UUID notExistingClientAppId = UUID.randomUUID();

		//when
		ClientApp result = testedRepo.getById(notExistingClientAppId);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getById_should_work_with_existing_client_id() {
		//given
		ClientApp expectedClientApp = TestHelper.generateRandomClientApp();
		testedRepo.create(expectedClientApp, "secret");

		//when
		ClientApp result = testedRepo.getById(expectedClientApp.getId());
		
		//then
		assertThat(result).isNotNull();
		Object salt = result.getSalt();
		String hash = testedRepo.hashSecret("secret", salt);
		expectedClientApp = ClientApp.Builder.createFrom(expectedClientApp).withSecret(hash).withSalt(salt).build();
		assertThat(result).isEqualTo(expectedClientApp);
	}
	
	@Test
	public void deleteById_should_not_fail_with_not_existing_client_id() {
		//given
		UUID notExistingClientAppId = UUID.randomUUID();

		//when
		testedRepo.deleteById(notExistingClientAppId);
		
		//then
		//should not have failed
	}
	
	@Test
	public void deleteById_should_work_with_existing_client_id() {
		//given
		ClientApp existingClientApp = TestHelper.generateRandomClientApp();
		testedRepo.create(existingClientApp, "secret");

		//when
		testedRepo.deleteById(existingClientApp.getId());
		
		//then
		ClientApp result = testedRepo.getById(existingClientApp.getId());
		assertThat(result).isNull();
	}
	
	@Test
	public void changeSecret_should_work_with_existing_client_app() throws InterruptedException {
		//given
		ClientApp originalClientApp = TestHelper.generateRandomClientApp();
		String originalPassword = "originalSecret";
		testedRepo.create(originalClientApp, originalPassword);
		originalClientApp = testedRepo.getById(originalClientApp.getId());
		String newPassword = "newSecret";
		Thread.sleep(1);

		//when
		testedRepo.changeSecret(originalClientApp.getId(), newPassword);
		
		//then
		ClientApp result = testedRepo.getById(originalClientApp.getId());
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(originalClientApp.getName());
		//creation date should not change
		assertThat(result.getCreationDate()).isEqualTo(originalClientApp.getCreationDate());
		//last update date should have changed
		assertThat(result.getLastUpdate().isAfter(originalClientApp.getLastUpdate())).isTrue();
		
		assertThat(result.getSecret()).isNotEqualTo(originalClientApp.getSecret());
		assertThat(result.getSalt()).isNotEqualTo(originalClientApp.getSalt());
	}
	
	@Test
	public void update_should_work_with_existing_client_app() throws InterruptedException {
		//given
		ClientApp originalClientApp = TestHelper.generateRandomClientApp();
		testedRepo.create(originalClientApp, "secret");
		originalClientApp = testedRepo.getById(originalClientApp.getId());
		String modifiedName = "new " + originalClientApp.getName();
		URI modifiedRedirectURI = URI.create("http://modified");
		ClientApp modifiedClientApp = ClientApp.Builder.createFrom(originalClientApp)
													.withName(modifiedName)
													.withRedirectURI(modifiedRedirectURI)
													.build();
		Thread.sleep(1);
		
		//when
		testedRepo.update(modifiedClientApp);
		
		//then
		ClientApp result = testedRepo.getById(originalClientApp.getId());
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(modifiedName);
		assertThat(result.getRedirectURI()).isEqualTo(modifiedRedirectURI);
		assertThat(result).isEqualTo(modifiedClientApp);
		//creation date should not change
		assertThat(result.getCreationDate()).isEqualTo(originalClientApp.getCreationDate());
		//last update date should have changed
		assertThat(result.getLastUpdate().isAfter(originalClientApp.getLastUpdate())).isTrue();
	}
	
	@Test
	public void getByOwner_should_return_empty_list_if_no_app_found() {
		//given
		UUID ownerIdWithoutApp = UUID.randomUUID();

		//when
		ImmutableList<ClientApp> result = testedRepo.getByOwner(ownerIdWithoutApp);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(ImmutableList.<ClientApp>of());
	}
	
	@Test
	public void getByOwner_should_work_with_owner_with_apps() {
		//given
		UUID ownerIdWithTwoApps = UUID.randomUUID();
		ClientApp app1 = TestHelper.generateRandomClientApp();
		ClientApp expectedClientApp1 = ClientApp.Builder.createFrom(app1).withOwnerId(ownerIdWithTwoApps).build();
		ClientApp app2 = TestHelper.generateRandomClientApp();
		ClientApp expectedClientApp2 = ClientApp.Builder.createFrom(app2).withOwnerId(ownerIdWithTwoApps).build();
		
		testedRepo.create(expectedClientApp1, "secret1");
		testedRepo.create(expectedClientApp2, "secret2");

		//when
		ImmutableList<ClientApp> result = testedRepo.getByOwner(ownerIdWithTwoApps);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(expectedClientApp1, expectedClientApp2);
	}
}