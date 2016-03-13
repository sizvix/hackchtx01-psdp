package fr.hackchtx01.site.representation;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.site.SiteUrl;

@XmlRootElement(name = "siteUrl")
public class SiteUrlRepresentation {
	private String type;
	private String url;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SiteUrlRepresentation.class);
	
	public SiteUrlRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public SiteUrlRepresentation(String type, String url) {
		this.type = type;
		this.url = url;
	}
	
	public SiteUrlRepresentation(SiteUrl siteUrl) {
		super();
		requireNonNull(siteUrl);
		this.type = siteUrl.getType();
		this.url = siteUrl.getUrl().toString();
	}
	
	public static SiteUrl toSiteUrl(SiteUrlRepresentation representation) {
		requireNonNull(representation, "Unable to create SiteUrl from null SiteUrlRepresentation");
		
		SiteUrl.Builder siteUrlBuilder = SiteUrl.Builder.createDefault()
						   .withType(representation.type)
						   .withUrl(URI.create(representation.url));
		
		SiteUrl site;
		try {
			site = siteUrlBuilder.build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("site url") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return site;
	}

	@XmlElement(name = "type")
	public String getType() {
		return type;
	}

	@XmlElement(name = "url")
	public String getUrl() {
		return url;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public static  List<SiteUrlRepresentation> extractUrlsRepresentations(ImmutableList<SiteUrl> urls) {
		List<SiteUrlRepresentation> siteUrls = new ArrayList<>();
		urls.forEach(url -> siteUrls.add(new SiteUrlRepresentation(url)));
		return siteUrls;
	}
	
	public static List<SiteUrl> toUrls(List<SiteUrlRepresentation> representations) {
		requireNonNull(representations, "Unable to create SiteUrls from null SiteUrlRepresentations");
		
		List<SiteUrl> urls = new ArrayList<>();
		representations.forEach(representation -> urls.add(toSiteUrl(representation)));
		
		return urls;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, url);
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SiteUrlRepresentation that = (SiteUrlRepresentation) obj;
        return Objects.equals(this.type, that.type)
                && Objects.equals(this.url, that.url);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("type", type)
											   .add("url", url)
											   .toString();
	}
}

