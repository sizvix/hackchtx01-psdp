/**
 * 
 */
package fr.hackchtx01.infra.util.helper;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.APPLICATION_ERROR;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.hackchtx01.infra.util.error.ApplicationException;

/**
 * Utility methods to convert properties to Objects
 * @author yoan
 */
public class PropertiesConverterHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConverterHelper.class);
	
	private PropertiesConverterHelper() { }
	
	public static String getMandatoryProperty(Properties properties, String fieldName) {
		String property = properties.getProperty(fieldName);
		if (StringUtils.isBlank(property)) {
			String message = "Missing mandatory property : " + fieldName; 
			LOGGER.error(message);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message);
		}
		return property;
	}
	
	public static Integer getMandatoryIntegerProperty(Properties properties, String fieldName) {
		String propertyStr = getMandatoryProperty(properties, fieldName);
		Integer property = null;
		try {
			property = Integer.parseInt(propertyStr);
		} catch(NumberFormatException e) {
			String message = "Invalid integer format for mandatory property : " + fieldName; 
			LOGGER.error(message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
		return property;
	}
	
	public static String getOptionalProperty(Properties properties, String fieldName) {
		String property = properties.getProperty(fieldName);
		if (StringUtils.isBlank(property)) {
			String message = "Missing optionnal property : " + fieldName + " => skipping it"; 
			LOGGER.info(message);
			return null;
		}
		return property;
	}
	
	public static Integer getOptionnalIntegerProperty(Properties properties, String fieldName) {
		String propertyStr = getOptionalProperty(properties, fieldName);
		if (propertyStr == null)
			return null;
		
		Integer property = null;
		try {
			property = Integer.parseInt(propertyStr);
		} catch(NumberFormatException e) {
			LOGGER.warn("Invalid integer format for optionnal property : " + fieldName + " => skipping it", e);
		}
		return property;
	}
}
