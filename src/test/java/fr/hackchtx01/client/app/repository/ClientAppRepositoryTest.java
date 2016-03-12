package fr.hackchtx01.client.app.repository;

import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorCode.UNSECURE_SECRET;
import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_SECRET_VALIDITY;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static fr.hackchtx01.test.TestHelper.assertApplicationException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.client.app.repository.fake.ClientAppFakeRepository;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.test.TestHelper;

@RunWith(MockitoJUnitRunner.class)
public class ClientAppRepositoryTest {
	@Spy
	ClientAppRepository testedRepo = getTestedRepository();
	
	protected ClientAppRepository getTestedRepository() {
		return new ClientAppFakeRepository();
	}
	
	@Test
	public void create_should_do_nothing_with_null_client_app() {
		//given
		ClientApp nullApp = null;

		//when
		testedRepo.create(nullApp, "secret");
		
		//then
		verify(testedRepo, never()).processCreate(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_null_secret() {
		//given
		ClientApp app = ClientApp.Builder.createDefault().withRandomId().build();
		String nullSecret = null;
		
		//when
		try {
			testedRepo.create(app, nullSecret);
		} catch(ApplicationException ae) {
			//then
			verify(testedRepo, never()).processCreate(any());
			assertApplicationException(ae, ERROR, UNSECURE_SECRET, PROBLEM_SECRET_VALIDITY);
			throw ae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_blank_secret() {
		//given
		ClientApp app = ClientApp.Builder.createDefault().withRandomId().build();
		String blankSecret = "  ";
		
		//when
		try {
			testedRepo.create(app, blankSecret);
		} catch(ApplicationException ae) {
			//then
			verify(testedRepo, never()).processCreate(any());
			assertApplicationException(ae, ERROR, UNSECURE_SECRET, PROBLEM_SECRET_VALIDITY);
			throw ae;
		}
	}
	
	@Test
	public void generateSalt_should_never_return_null() {
		//when
		Object salt = testedRepo.generateSalt();
		
		//then
		assertThat(salt).isNotNull();
	}
	
	@Test
	public void generateSalt_should_never_return_same_value() {
		//when
		Object salt1 = testedRepo.generateSalt();
		Object salt2 = testedRepo.generateSalt();
		
		//then
		assertThat(salt1).isNotEqualTo(salt2);
	}
	
	@Test
	public void hashSecret_should_always_return_the_same_hash_if_using_the_same_salt() {
		//given
		Object salt = "salt";
		String secret = "secret";
		
		//when
		String hash1 = testedRepo.hashSecret(secret, salt);
		String hash2 = testedRepo.hashSecret(secret, salt);
		String hash3 = testedRepo.hashSecret(secret, salt);
		
		assertThat(hash1).isNotNull();
		assertThat(hash1).isEqualTo(hash2);
		assertThat(hash1).isEqualTo(hash3);
	}
	
	@Test
	public void hashSecret_should_not_return_the_same_hash_if_using_different_salt() {
		//given
		String secret = "secret";
		
		//when
		String hash1 = testedRepo.hashSecret(secret, UUID.randomUUID().toString());
		String hash2 = testedRepo.hashSecret(secret, UUID.randomUUID().toString());
		String hash3 = testedRepo.hashSecret(secret, UUID.randomUUID().toString());
		
		assertThat(hash1).isNotEqualTo(hash2);
		assertThat(hash1).isNotEqualTo(hash3);
		assertThat(hash2).isNotEqualTo(hash3);
	}
	
	@Test
	public void getById_should_return_null_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		ClientApp result = testedRepo.getById(nullId);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetById(any());
	}
	
	@Test
	public void getByOwner_should_return_empty_list_with_null_owner_Id() {
		//given
		UUID nullId = null;

		//when
		ImmutableList<ClientApp> result = testedRepo.getByOwner(nullId);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		verify(testedRepo, never()).processGetByOwner(any());
	}
	
	@Test
	public void changeSecret_should_do_nothing_with_null_client_Id() {
		//given
		UUID nullClientId = null;

		//when
		testedRepo.changeSecret(nullClientId, "secret");
		
		//then
		verify(testedRepo, never()).processChangeSecret(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void changeSecret_should_fail_with_not_existing_client_app() {
		//given
		UUID notExistingClientId = UUID.randomUUID();

		//when
		try {
			testedRepo.changeSecret(notExistingClientId, "secret");
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, "Client app not found");
			throw ae;
		} finally {
			verify(testedRepo, never()).processChangeSecret(any());
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void changeSecret_should_fail_with_blank_password() {
		//given
		UUID clientId = UUID.randomUUID();
		String blankSecret = "  ";
		
		//when
		try {
			testedRepo.changeSecret(clientId, blankSecret);
		} catch(ApplicationException ae) {
			//then
			verify(testedRepo, never()).processChangeSecret(any());
			assertApplicationException(ae, ERROR, UNSECURE_SECRET, PROBLEM_SECRET_VALIDITY);
			throw ae;
		}
	}
	
	@Test
	public void update_should_do_nothing_with_null_client_app() {
		//given
		ClientApp nullClientApp = null;

		//when
		testedRepo.update(nullClientApp);
		
		//then
		verify(testedRepo, never()).processUpdate(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_fail_with_not_existing_client_app() {
		//given
		ClientApp notExistingClientApp = TestHelper.generateRandomClientApp();

		//when
		try {
			testedRepo.update(notExistingClientApp);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, "Client app not found");
			throw ae;
		} finally {
			verify(testedRepo, never()).processUpdate(any());
		}
	}
	
	@Test
	public void deleteById_should_do_nothing_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		testedRepo.deleteById(nullId);
		
		//then
		verify(testedRepo, never()).processDeleteById(any());
	}
}
