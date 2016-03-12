/**
 * 
 */
package fr.hackchtx01.infra.rest.error;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author yoan
 */
@XmlRootElement
public class ErrorRepresentation {
	/** Level of error */
	private Level level;
	/** Error code */
	private String code;
	/** human readable error message */
	private String message;
	
	public ErrorRepresentation() {
	}
	
	public ErrorRepresentation(Level level, String code, String message) {
		this.level = requireNonNull(level, "ErrorRepresentation must have a criticity level");
		checkArgument(StringUtils.isNotBlank(code), "ErrorRepresentation must have an error code");
		this.code = code;
		checkArgument(StringUtils.isNotBlank(message), "ErrorRepresentation must have a human readable error message");
		this.message = message;
	}

	@XmlElement(name = "level")
	public Level getLevel() {
		return level;
	}

	@XmlElement(name = "code")
	public String getCode() {
		return code;
	}

	@XmlElement(name = "message")
	public String getMessage() {
		return message;
	}
}
