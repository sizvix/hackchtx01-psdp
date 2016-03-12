/**
 * 
 */
package fr.hackchtx01.infra.util.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility methods to handle dates
 * @author yoan
 */
public class DateHelper {
	
	private DateHelper() { }
	
	/**
	 * Convert a LocalDateTime to a Date
	 * @param localDateTime LocalDateTime to convert
	 * @return Date or null if localDateTime was null
	 */
	public static  Date toDate(LocalDateTime localDateTime) {
		return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Convert a Date to a LocalDateTime
	 * @param date Date to convert
	 * @return LocalDateTime or null if date was null
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
}
