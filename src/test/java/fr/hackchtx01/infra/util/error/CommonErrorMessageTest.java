/**
 * 
 */
package fr.hackchtx01.infra.util.error;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import fr.hackchtx01.infra.util.error.CommonErrorMessage;

public class CommonErrorMessageTest {

	@Test
	public void getHumanReadableMessage_should_work_with_varargs() {
		//given
		CommonErrorMessage messageWithVarargs = CommonErrorMessage.INVALID;
		String expectedMessage = "Invalid thing";
		
		//when
		String result = messageWithVarargs.getDevReadableMessage("thing");
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedMessage);
	}
	
	@Test
	public void getHumanReadableMessage_should_not_fail_with_varargs_message_without_param() {
		//given
		CommonErrorMessage messageWithVarargs = CommonErrorMessage.INVALID;
		String expectedMessage = "Invalid %s";
		
		//when
		String result = messageWithVarargs.getDevReadableMessage();
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedMessage);
	}
}
