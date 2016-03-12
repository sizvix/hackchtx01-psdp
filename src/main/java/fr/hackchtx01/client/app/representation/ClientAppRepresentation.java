package fr.hackchtx01.client.app.representation;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.resource.ClientAppResource;
import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.RestRepresentation;
import fr.hackchtx01.infra.rest.error.WebApiException;

/**
 * Client application Rest Representation
 * @author yoan
 */
@XmlRootElement(name = "clientApp")
public class ClientAppRepresentation extends RestRepresentation {
	/** Client app unique ID */
	private UUID id;
	/** Client app last name */
	private String name;
	/** Oauth2 redirect URI */
	private String redirectURI;
	/** Client app owner ID */
	private UUID ownerId;
	/** Client app secret key */
	private String secretKey;
	/** Client app creation date */
	private LocalDateTime creationDate;
	/** Last time the client app was updated */
	private LocalDateTime lastUpdate;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientAppRepresentation.class);
	
	public ClientAppRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public ClientAppRepresentation(UUID id, String name, UUID ownerId, String redirectURI, String secretKey, List<Link> links) {
		super(links);
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		this.redirectURI = redirectURI;
		this.secretKey = secretKey;
	}
	
	public ClientAppRepresentation(ClientApp clientApp, UriInfo uriInfo) {
		super();
		requireNonNull(clientApp);
		requireNonNull(uriInfo);
		URI selfURI = uriInfo.getBaseUriBuilder().path(ClientAppResource.class).path(ClientAppResource.class, "getById").build(clientApp.getId());
		this.links.add(Link.self(selfURI));
		this.id = clientApp.getId();
		this.name = clientApp.getName();
		this.ownerId = clientApp.getOwnerId();
		this.redirectURI = clientApp.getRedirectURI().toString();
		this.creationDate = clientApp.getCreationDate();
		this.lastUpdate = clientApp.getLastUpdate();
		//should be set using setter
		this.secretKey = null;
	}
	
	public static ClientApp toClientApp(ClientAppRepresentation representation) {
		requireNonNull(representation, "Unable to create client application from null ClientAppRepresentation");
		
		ClientApp app;
		try {
			ClientApp.Builder appBuilder = ClientApp.Builder.createDefault()
							   .withName(representation.name)
							   .withOwnerId(representation.ownerId)
							   .withRedirectURI(URI.create(representation.redirectURI));
			//if no ID provided, we let the default one
			if (representation.id != null) {
				appBuilder.withId(representation.id);
			}
		
			app = appBuilder.build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("client application") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return app;
	}

	@XmlElement(name = "id")
	public UUID getId() {
		return id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	@XmlElement(name = "ownerid")
	public UUID getOwnerId() {
		return ownerId;
	}

	@XmlElement(name = "redirectURI")
	public String getRedirectURI() {
		return redirectURI;
	}
	
	@XmlElement(name = "secretkey")
	public String getSecretkey() {
		return secretKey;
	}
	
	@XmlElement(name = "creationDate")
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	
	@XmlElement(name = "lastUpdate")
	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
	}

	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}
	
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, ownerId, redirectURI, creationDate, lastUpdate);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClientAppRepresentation that = (ClientAppRepresentation) obj;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.ownerId, that.ownerId)
                && Objects.equals(this.redirectURI, that.redirectURI)
                && Objects.equals(this.creationDate, that.creationDate)
                && Objects.equals(this.lastUpdate, that.lastUpdate);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id)
											   .add("name", name)
											   .add("ownerId", ownerId)
											   .add("redirectURI", redirectURI)
											   .add("created", creationDate)
											   .add("lastUpdate", lastUpdate)
											   .toString();
	}
}
