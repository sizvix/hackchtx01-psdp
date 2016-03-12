package fr.hackchtx01.infra.util.helper;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static fr.hackchtx01.test.TestHelper.assertApplicationException;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.helper.PropertiesConverterHelper;

public class PropertiesConverterHelperTest {
	@Test(expected = ApplicationException.class)
	public void getMandatoryProperty_should_fail_if_properties_does_not_contain_field() {
		//given
		Properties properties = new Properties();
		String notPresentField = "notPresentField";
		String expectedMessage = "Missing mandatory property : " + notPresentField;
		
		//when
		try {
			PropertiesConverterHelper.getMandatoryProperty(properties, notPresentField);
		} catch(ApplicationException ae) {
		//then
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedMessage);
			throw ae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void getMandatoryProperty_should_fail_if_properties_contains_blank_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		properties.setProperty(fieldName, "  ");
		String expectedMessage = "Missing mandatory property : " + fieldName;
		
		//when
		try {
			PropertiesConverterHelper.getMandatoryProperty(properties, fieldName);
		} catch(ApplicationException ae) {
		//then
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedMessage);
			throw ae;
		}
	}
	
	@Test
	public void getMandatoryProperty_should_work_if_properties_contains_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		String expected = "bla";
		properties.setProperty(fieldName, expected);
		
		
		//when
		String result = PropertiesConverterHelper.getMandatoryProperty(properties, fieldName);

		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expected);
	}
	
	@Test(expected = ApplicationException.class)
	public void getMandatoryIntegerProperty_should_fail_if_properties_does_not_contain_field() {
		//given
		Properties properties = new Properties();
		String notPresentField = "notPresentField";
		String expectedMessage = "Missing mandatory property : " + notPresentField;
		
		//when
		try {
			PropertiesConverterHelper.getMandatoryIntegerProperty(properties, notPresentField);
		} catch(ApplicationException ae) {
		//then
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedMessage);
			throw ae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void getMandatoryIntegerProperty_should_fail_if_properties_contains_blank_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		properties.setProperty(fieldName, "  ");
		String expectedMessage = "Missing mandatory property : " + fieldName;
		
		//when
		try {
			PropertiesConverterHelper.getMandatoryIntegerProperty(properties, fieldName);
		} catch(ApplicationException ae) {
		//then
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedMessage);
			throw ae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void getMandatoryIntegerProperty_should_fail_if_properties_contains_invalid_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		properties.setProperty(fieldName, "five");
		String expectedMessage = "Invalid integer format for mandatory property : " + fieldName;
		
		//when
		try {
			PropertiesConverterHelper.getMandatoryIntegerProperty(properties, fieldName);
		} catch(ApplicationException ae) {
		//then
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedMessage);
			throw ae;
		}
	}
	
	@Test
	public void getMandatoryIntegerProperty_should_work_if_properties_contains_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		int expected = 5;
		properties.setProperty(fieldName, String.valueOf(expected));
		
		
		//when
		int result = PropertiesConverterHelper.getMandatoryIntegerProperty(properties, fieldName);

		//then
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void getOptionalProperty_should_return_null_if_properties_does_not_contain_field() {
		//given
		Properties properties = new Properties();
		String notPresentField = "notPresentField";
		
		//when
		String result = PropertiesConverterHelper.getOptionalProperty(properties, notPresentField);

		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getOptionalProperty_should_return_null_if_properties_contains_blank_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		properties.setProperty(fieldName, "  ");
		
		//when
		String result = PropertiesConverterHelper.getOptionalProperty(properties, fieldName);

		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getOptionalProperty_should_work_if_properties_contains_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		String expected = "bla";
		properties.setProperty(fieldName, expected);
		
		//when
		String result = PropertiesConverterHelper.getOptionalProperty(properties, fieldName);

		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void getOptionnalIntegerProperty_should_return_null_if_properties_does_not_contain_field() {
		//given
		Properties properties = new Properties();
		String notPresentField = "notPresentField";
		
		//when
		Integer result = PropertiesConverterHelper.getOptionnalIntegerProperty(properties, notPresentField);

		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getOptionnalIntegerProperty_should_return_null_if_properties_contains_blank_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		properties.setProperty(fieldName, "  ");
		
		//when
		Integer result = PropertiesConverterHelper.getOptionnalIntegerProperty(properties, fieldName);

		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void  getOptionnalIntegerProperty_should_return_null_if_properties_contains_invalid_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		properties.setProperty(fieldName, "five");
		
		//when
		Integer result = PropertiesConverterHelper.getOptionnalIntegerProperty(properties, fieldName);

		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getOptionnalIntegerProperty_should_work_if_properties_contains_field() {
		//given
		String fieldName = "fieldName";
		Properties properties = new Properties();
		int expected = 5;
		properties.setProperty(fieldName, String.valueOf(expected));
		
		//when
		int result = PropertiesConverterHelper.getOptionnalIntegerProperty(properties, fieldName);

		//then
		assertThat(result).isEqualTo(expected);
	}
}
