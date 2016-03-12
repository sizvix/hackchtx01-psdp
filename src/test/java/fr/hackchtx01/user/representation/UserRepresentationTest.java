package fr.hackchtx01.user.representation;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.google.common.collect.Lists;

import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.representation.UserRepresentation;

public class UserRepresentationTest {
	
	@Test(expected = NullPointerException.class)
	public void userRepresentation_should_fail_without_user() {
		//given
		User nullUser = null;
		
		//when
		new UserRepresentation(nullUser, mock(UriInfo.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void userRepresentation_should_fail_without_UriInfo() {
		//given
		UriInfo nullUriInfo = null;
		
		//when
		new UserRepresentation(TestHelper.generateRandomUser(), nullUriInfo);
	}
	
	@Test
	public void userRepresentation_should_contains_user_self_link() {
		//given
		User user = TestHelper.generateRandomUser();
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		
		//when
		UserRepresentation result = new UserRepresentation(user, mockedUriInfo);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(user.getId());
		assertThat(result.getLinks()).isNotNull();
		assertThat(result.getLinks()).isNotEmpty();
		assertThat(result.getLinks()).contains(Link.self(expectedURL));
	}
	
	@Test(expected = NullPointerException.class)
	public void toUser_should_fail_without_representation() {
		//given
		UserRepresentation nullRepresentation = null;
		
		//when
		try {
			UserRepresentation.toUser(nullRepresentation);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create User from null UserRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toUser_should_fail_with_invalid_Representation() {
		//given
		@SuppressWarnings("deprecation")
		UserRepresentation invalidUserRepresentation = new UserRepresentation(UUID.randomUUID(), " ", " ", Lists.newArrayList());
		String expectedMessage = INVALID.getDevReadableMessage("user") + " : Invalid user name";
		
		//when
		try {
			UserRepresentation.toUser(invalidUserRepresentation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void toUser_should_work() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		@SuppressWarnings("deprecation")
		UserRepresentation validUserRepresentation = new UserRepresentation(expectedUser.getId(), expectedUser.getName(), expectedUser.getEmail(), Lists.newArrayList());
		
		//when
		User result = UserRepresentation.toUser(validUserRepresentation);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
	}
}
