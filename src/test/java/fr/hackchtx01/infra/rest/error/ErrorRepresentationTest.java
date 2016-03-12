package fr.hackchtx01.infra.rest.error;

import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import fr.hackchtx01.infra.rest.error.ErrorRepresentation;
import fr.hackchtx01.infra.rest.error.Level;

public class ErrorRepresentationTest {

	@Test(expected = NullPointerException.class)
	public void errorRepresentation_should_fail_fast_if_null_level() {
		//given
		Level nullLevel = null;
		
		//when
		try {
			new ErrorRepresentation(nullLevel, "code", "message");
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("ErrorRepresentation must have a criticity level");
			throw npe;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void errorRepresentation_should_fail_fast_if_null_code() {
		//given
		String nullCode = null;
		
		//when
		try {
			new ErrorRepresentation(INFO, nullCode, "message");
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("ErrorRepresentation must have an error code");
			throw iae;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void errorRepresentation_should_fail_fast_if_blank_code() {
		//given
		String blankCode = "  ";
		
		//when
		try {
			new ErrorRepresentation(INFO, blankCode, "message");
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("ErrorRepresentation must have an error code");
			throw iae;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void errorRepresentation_should_fail_fast_if_null_message() {
		//given
		String nullMessage = null;
		
		//when
		try {
			new ErrorRepresentation(INFO, "code", nullMessage);
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("ErrorRepresentation must have a human readable error message");
			throw iae;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void errorRepresentation_should_fail_fast_if_blank_message() {
		//given
		String blankMessage = "  ";
		
		//when
		try {
			new ErrorRepresentation(INFO, "code", blankMessage);
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo("ErrorRepresentation must have a human readable error message");
			throw iae;
		}
	}
}
