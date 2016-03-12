package fr.hackchtx01.user;

import static org.fest.assertions.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import fr.hackchtx01.user.User;

public class UserTest {

	@Test(expected = NullPointerException.class)
	public void user_should_fail_without_Id() {
		//given
		UUID nullId = null;
		
		//when
		try {
			new User(nullId, "name", "mail", LocalDateTime.now(), LocalDateTime.now());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("User Id is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void user_should_fail_with_blank_name() {
		//given
		String blankName = "  ";
		
		//when
		try {
			new User(UUID.randomUUID(), blankName, "mail", LocalDateTime.now(), LocalDateTime.now());
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("Invalid user name");
			throw iae;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void user_should_fail_without_creation_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new User(UUID.randomUUID(), "name", "mail", nullDate, LocalDateTime.now());
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Creation date is mandatory");
			throw npe;
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void user_should_fail_without_last_update_date() {
		//given
		LocalDateTime nullDate = null;
		
		//when
		try {
			new User(UUID.randomUUID(), "name", "mail", LocalDateTime.now(), nullDate);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Last update date is mandatory");
			throw npe;
		}
	}
}
