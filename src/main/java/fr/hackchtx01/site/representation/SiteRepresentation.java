package fr.hackchtx01.site.representation;

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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.RestRepresentation;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.site.Site;
import fr.hackchtx01.site.resource.SiteResource;

@XmlRootElement(name = "site")
public class SiteRepresentation extends RestRepresentation {
	/** Site unique ID */
	private UUID id;
	/** Site last name */
	private String name;
	/** Site url */
	private String url;
	/** site creation date */
	private LocalDateTime creationDate;
	/** Last time the site was updated */
	private LocalDateTime lastUpdate;
	private List<SiteUrlRepresentation> urls;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SiteRepresentation.class);
	
	public SiteRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public SiteRepresentation(UUID id, String name, String url, List<SiteUrlRepresentation> urls, List<Link> links) {
		super(links);
		this.id = id;
		this.name = name;
		this.url = url;
		this.urls = urls;
	}
	
	public SiteRepresentation(Site site, UriInfo uriInfo) {
		super();
		requireNonNull(site);
		requireNonNull(uriInfo);
		URI selfURI = uriInfo.getAbsolutePathBuilder().path(SiteResource.class).path(SiteResource.class, "getById").build(site.getId());
		this.links.add(Link.self(selfURI));
		this.id = site.getId();
		this.name = site.getName();
		this.url = site.getUrl().toString();
		this.creationDate = site.getCreationDate();
		this.lastUpdate = site.getLastUpdate();
		this.urls = SiteUrlRepresentation.extractUrlsRepresentations(site.getUrls());
	}
	
	public static Site toSite(SiteRepresentation representation) {
		requireNonNull(representation, "Unable to create Site from null SiteRepresentation");
		
		Site.Builder siteBuilder = Site.Builder.createDefault()
						   .withName(representation.name)
						   .withUrl(URI.create(representation.url))
						   .withUrls(SiteUrlRepresentation.toUrls(representation.getUrls()));
		//if no ID provided, we let the default one
		if (representation.id != null) {
			siteBuilder.withId(representation.id);
		}
		
		Site site;
		try {
			site = siteBuilder.build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("site") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return site;
	}

	@XmlElement(name = "id")
	public UUID getId() {
		return id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "url")
	public String getUrl() {
		return url;
	}
	
	@XmlElementWrapper(name = "urls")
	@XmlElement(name = "url")
	public List<SiteUrlRepresentation> getUrls() {
		return urls;
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

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public void setItemList(List<SiteUrlRepresentation> urls) {
		this.urls = urls;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, url, creationDate, lastUpdate);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SiteRepresentation that = (SiteRepresentation) obj;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.url, that.url)
                && Objects.equals(this.creationDate, that.creationDate)
                && Objects.equals(this.lastUpdate, that.lastUpdate);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id)
											   .add("name", name)
											   .add("url", url)
											   .add("created", creationDate)
											   .add("lastUpdate", lastUpdate)
											   .add("urls", urls)
											   .toString();
	}
}
