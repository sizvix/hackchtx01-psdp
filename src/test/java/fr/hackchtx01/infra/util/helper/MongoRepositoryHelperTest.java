package fr.hackchtx01.infra.util.helper;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static fr.hackchtx01.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.mongodb.MongoException;

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.helper.MongoRepositoryHelper;
import fr.hackchtx01.test.TestHelper;

@RunWith(MockitoJUnitRunner.class)
public class MongoRepositoryHelperTest {
	
	@Mock
	private Logger logger;
	
	@Test(expected = ApplicationException.class)
	public void handleMongoError_should_throw_error() {
		//given
		String expectionMessage = "message";
		MongoException exception = new MongoException(expectionMessage);
		String expectedmessage = PROBLEM_CREATION_USER.getDevReadableMessage(expectionMessage);
		
		//when
		try {
			MongoRepositoryHelper.handleMongoError(logger, exception, PROBLEM_CREATION_USER);
		} catch(ApplicationException ae) {
		//then
			TestHelper.assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedmessage);
			throw ae;
		}
	}
}
