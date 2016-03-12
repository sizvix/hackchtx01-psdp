/**
 * 
 */
package fr.hackchtx01.root;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects;

/**
 * Informations about build
 * @author yoan
 */
public class BuildInfo {
	/** Default version */
	public static final String DEFAULT_VERSION = "UNKNOWN";
	/** Default build info */
	public static final BuildInfo DEFAULT = new BuildInfo(DEFAULT_VERSION, null);
	
	/** Current build version */
	private final String version;
	/** Maven build date */
	private final LocalDateTime buildDate;
	
	public BuildInfo(String version, LocalDateTime buildDate) {
		checkArgument(StringUtils.isNotBlank(version), "The version field is mandatory");
		this.version = version;
		this.buildDate = buildDate;
	}

	public String getVersion() {
		return version;
	}

	public LocalDateTime getBuildDate() {
		return buildDate;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(version, buildDate);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BuildInfo that = (BuildInfo) obj;
        return Objects.equals(this.version, that.version)
                && Objects.equals(this.buildDate, that.buildDate);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("version", version)
											   .add("buildDate", buildDate)
											   .toString();
	}
}
