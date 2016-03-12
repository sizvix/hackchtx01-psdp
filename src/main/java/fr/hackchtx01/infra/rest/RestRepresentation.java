package fr.hackchtx01.infra.rest;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

/**
 * Basic representation with the minimum fields
 * @author yoan
 */
@XmlRootElement(name = "root")
public class RestRepresentation {
	/** Rest navigation links */
	protected final List<Link> links;
	
	public RestRepresentation() {
		links = Lists.newArrayList();
	}
	
	public RestRepresentation(List<Link> links) {
		this.links = requireNonNull(links, "A rest representation should always have navigation links");
	}
	
	@XmlElementWrapper(name = "links")
	@XmlElement(name = "link")
	public List<Link> getLinks() {
		return links;
	}
	
}
