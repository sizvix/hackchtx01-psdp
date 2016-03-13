package fr.hackchtx01.site.repository;

import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static fr.hackchtx01.site.resource.SiteResourceErrorMessage.SITE_NOT_FOUND;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.site.Site;
import fr.hackchtx01.site.SiteUrl;

public abstract class SiteRepository {
public static final Logger LOGGER = LoggerFactory.getLogger(SiteRepository.class);
	
	/**
	 * Create a new Site
	 * @param askedSiteToCreate
	 */
	public final void create(Site askedSiteToCreate) {
		if (askedSiteToCreate == null) {
			LOGGER.warn("Site creation asked with null site");
			return;
		}
		
		Site siteToCreate = forceCreationDate(askedSiteToCreate);
		processCreate(siteToCreate);
	}
	
	private Site forceCreationDate(Site site) {
		LocalDateTime creationDate = LocalDateTime.now();
		return Site.Builder.createFrom(site)
			.withCreationDate(creationDate)
			.withLastUpdate(creationDate)
			.build();
	}
	
	/**
	 * Get a site by its Id
	 * @param siteId
	 * @return found site or null if not found
	 */
	public final Site getById(UUID siteId) {
		if (siteId == null) {
			return null;
		}
		return processGetById(siteId);
	}
	
	/**
	 * Update a Site
	 * @param askedSiteToUpdate
	 */
	public final void update(Site askedSiteToUpdate) {
		if (askedSiteToUpdate == null) {
			LOGGER.warn("Site update asked with null site");
			return;
		}
		Site existingSite = findSite(askedSiteToUpdate.getId());
		
		Site siteToUpdate = mergeUpdatesInExistingSite(existingSite, askedSiteToUpdate);
		processUpdate(siteToUpdate);
	}
	
	private Site mergeUpdatesInExistingSite(Site existingSite, Site askedSiteToUpdate) {
		return Site.Builder.createFrom(existingSite)
				.withLastUpdate(LocalDateTime.now())
				.withName(askedSiteToUpdate.getName())
				.withUrl(askedSiteToUpdate.getUrl())
				.build();
	}
	
	/**
	 * Get a site by its Id and fail if it does not exist
	 * @param siteId
	 * @return found site
	 * @throws ApplicationException if site not found
	 */
	public final Site findSite(UUID siteId) {
		Site foundSite = getById(siteId);
		
		if (foundSite == null) {
			throw new ApplicationException(INFO, NOT_FOUND, SITE_NOT_FOUND);
		}
		
		return foundSite;
	}
	
	
	/**
	 * Delete a site by its Id
	 * @param siteId
	 */
	public final void deleteById(UUID siteId) {
		if (siteId == null) {
			LOGGER.warn("Site deletion asked with null Id");
			return;
		}
		processDeleteById(siteId);
	}
	
	public final void addUrl(UUID siteId, SiteUrl urlToCreate) {
		if (siteId == null) {
			LOGGER.warn("Site URL creation asked with null site ID");
			return;
		}
		if (urlToCreate == null) {
			LOGGER.warn("Site URL creation asked with null site URL");
			return;
		}
		
		processAddUrl(siteId, urlToCreate);
	}
	
	public final Site findByURL(URI url) {
		if (url == null) {
			LOGGER.warn("Site URL search asked with null site URL");
			return null ;
		}
		return processFindByURL(url.getHost());
	}
	
	protected abstract Site processFindByURL(String string);

	/**
	 * Create a new site
	 * @param siteToCreate
	 */
	protected abstract void processCreate(Site siteToCreate);
	
	/**
	 * Get a site by its Id
	 * @param siteId
	 */
	protected abstract Site processGetById(UUID siteId);

	/**
	 * Update an existing site
	 * @param siteToUpdate
	 */
	protected abstract void processUpdate(Site siteToUpdate);
	
	/**
	 * Delete a site by its Id
	 * @param siteId
	 */
	protected abstract void processDeleteById(UUID siteId);
	
	protected abstract void processAddUrl(UUID siteId, SiteUrl urlToCreate);
}
