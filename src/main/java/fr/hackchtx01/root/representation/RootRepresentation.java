/**
 * 
 */
package fr.hackchtx01.root.representation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;

import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.RestRepresentation;
import fr.hackchtx01.root.BuildInfo;
import fr.hackchtx01.user.User;

/**
 * Rest Root representation
 * @author yoan
 */
@XmlRootElement(name = "root")
public class RootRepresentation extends RestRepresentation {
	/** Current build version */
	private String version;
	/** Maven build date */
	private LocalDateTime buildDate;
	/** Connected user Id */
	private UUID connectedUserId;
	
	public RootRepresentation() {
		super();
		this.version = "version";
		this.buildDate = LocalDateTime.now();
		this.connectedUserId = User.DEFAULT_ID;
	}
	
	public RootRepresentation(BuildInfo buildInfo, UUID connectedUserId, List<Link> links) {
		super(links);
		if (buildInfo != null) {
			this.version = buildInfo.getVersion();
			this.buildDate = buildInfo.getBuildDate();
		}
		this.connectedUserId = connectedUserId;
	}

	@XmlElement(name = "version")
	public String getVersion() {
		return version;
	}

	@XmlElement(name = "buildDate")
	public LocalDateTime getBuildDate() {
		return buildDate;
	}
	
	@XmlElement(name = "connectedUserId")
	public UUID getConnectedUserId() {
		return connectedUserId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), version, buildDate);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RootRepresentation that = (RootRepresentation) obj;
        return super.equals(obj) && Objects.equals(this.version, that.version)
                && Objects.equals(this.buildDate, that.buildDate)
                && Objects.equals(this.connectedUserId, that.connectedUserId);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("version", version)
											   .add("buildDate", buildDate)
										   	   .add("connectedUserId", connectedUserId)
											   .toString();
	}
}
