/**
 * 
 */
package fr.hackchtx01.infra.logging;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Enumeration of all the defined SLF4J markers
 * @author yoan
 */
public enum Markers {
	/** Authentication trace */
	AUTHENTICATION("Auth"),
	/** Configuration trace */
	CONFIG("Config"),
	/** Security related trace */
	SECURITY("Security");
	
	private final Marker marker;
	
	private Markers(String markerName) {
		checkArgument(StringUtils.isNotBlank(markerName), "A marker name should not be empty");
		marker = MarkerFactory.getMarker(markerName);
	}

	public Marker getMarker() {
		return marker;
	}
}
