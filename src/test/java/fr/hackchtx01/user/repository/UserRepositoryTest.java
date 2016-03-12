package fr.hackchtx01.user.repository;

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

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.UserRepository;
import fr.hackchtx01.user.repository.fake.UserFakeRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {
	
	@Spy
	UserRepository testedRepo = new UserFakeRepository();
	
	@Test
	public void create_should_do_nothing_with_null_user() {
		//given
		User nullUser = null;

		//when
		testedRepo.create(nullUser);
		
		//then
		verify(testedRepo, never()).processCreate(any());
	}
	
	@Test
	public void getById_should_return_null_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		User result = testedRepo.getById(nullId);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetById(any());
	}
	
	@Test
	public void update_should_do_nothing_with_null_user() {
		//given
		User nullUser = null;

		//when
		testedRepo.update(nullUser);
		
		//then
		verify(testedRepo, never()).processUpdate(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_fail_with_not_existing_user() {
		//given
		User notExistingUser = TestHelper.generateRandomUser();

		//when
		try {
			testedRepo.update(notExistingUser);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, "User not found");
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
	
	@Test
	public void getByEmail_should_return_null_with_null_email() {
		//given
		String nullEmail = null;

		//when
		User result = testedRepo.getByEmail(nullEmail);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetByEmail(any());
	}
	
	@Test
	public void getByEmail_should_return_null_with_blank_email() {
		//given
		String blankEmail = "  ";

		//when
		User result = testedRepo.getByEmail(blankEmail);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetByEmail(any());
	}
}
