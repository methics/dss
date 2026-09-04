/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.validation.job.cache.access;

import eu.europa.esig.dss.model.job.DownloadInfoRecord;
import eu.europa.esig.dss.model.job.ParsingInfoRecord;
import eu.europa.esig.dss.model.job.ValidationInfoRecord;
import eu.europa.esig.dss.validation.job.cache.CacheKey;
import eu.europa.esig.dss.validation.job.cache.DownloadCache;
import eu.europa.esig.dss.validation.job.cache.ParsingCache;
import eu.europa.esig.dss.validation.job.cache.ValidationCache;
import eu.europa.esig.dss.validation.job.download.DownloadResult;
import eu.europa.esig.dss.validation.job.parsing.ParsingResult;
import eu.europa.esig.dss.validation.job.validation.ValidationResult;

/**
 * Abstract implementation to access a cache record by a specified key
 *
 * @param <D> {@link DownloadInfoRecord}
 * @param <P> {@link ParsingInfoRecord}
 * @param <V> {@link ValidationInfoRecord}
 */
public abstract class AbstractCacheAccessByKey<D extends DownloadInfoRecord, P extends ParsingInfoRecord, V extends ValidationInfoRecord>
		extends AbstractReadOnlyCacheAccessByKey<D, P, V> implements CacheAccessByKey {

	/** Global download Cache */
	private final DownloadCache downloadCache;

	/** Global parsing Cache */
	private final ParsingCache parsingCache;

	/** Global validation Cache */
	private final ValidationCache validationCache;

	/**
	 * Default constructor
	 *
	 * @param key {@link CacheKey} to use
	 * @param downloadCache {@link DownloadCache}
	 * @param parsingCache {@link ParsingCache}
	 * @param validationCache {@link ValidationCache}
	 * @param readOnlyCacheAccess {@link ParametrizedReadOnlyCacheAccess}
	 */
	protected AbstractCacheAccessByKey(final CacheKey key, final DownloadCache downloadCache, final ParsingCache parsingCache,
	                                   final ValidationCache validationCache, final ParametrizedReadOnlyCacheAccess<D, P, V> readOnlyCacheAccess) {
		super(key, readOnlyCacheAccess);
		this.downloadCache = downloadCache;
		this.parsingCache = parsingCache;
		this.validationCache = validationCache;
	}
	
	/**
	 * Returns the CacheKey
	 *
	 * @return {@link CacheKey}
	 */
	public CacheKey getCacheKey() {
		return key;
	}

	/**
	 * Checks if the download result is up to date for the given key
	 *
	 * @param DownloadResult {@link DownloadResult}
	 * @return TRUE if the download result matches, FALSE otherwise
	 */
	public boolean isUpToDate(DownloadResult DownloadResult) {
		return downloadCache.isUpToDate(key, DownloadResult);
	}

	/**
	 * Updates the download result
	 *
	 * @param result {@link DownloadResult} to store
	 */
	public void update(DownloadResult result) {
		downloadCache.update(key, result);
	}

	/**
	 * Sets the download error
	 *
	 * @param e {@link Exception}
	 */
	public void downloadError(Exception e) {
		downloadCache.error(key, e);
	}

	/**
	 * Gets of the parsing refresh is needed
	 *
	 * @return TRUE if the parsing refresh is needed, FALSE otherwise
	 */
	public boolean isParsingRefreshNeeded() {
		return parsingCache.isRefreshNeeded(key);
	}

	/**
	 * Updates the parsing result
	 *
	 * @param parsingResult {@link ParsingResult} to store
	 */
	public void update(ParsingResult parsingResult) {
		parsingCache.update(key, parsingResult);
	}

	/**
	 * Sets the parsing record to the expired state
	 */
	public void expireParsing() {
		parsingCache.expire(key);
	}

	/**
	 * Sets the parsing error
	 *
	 * @param e {@link Exception}
	 */
	public void parsingError(Exception e) {
		parsingCache.error(key, e);
	}

	/**
	 * Gets if the validation refresh is needed
	 *
	 * @return TRUE if the validation refresh is needed, FALSE otherwise
	 */
	public boolean isValidationRefreshNeeded() {
		return validationCache.isRefreshNeeded(key);
	}

	/**
	 * Expires the validation record
	 */
	public void expireValidation() {
		validationCache.expire(key);
	}

	/**
	 * Updates the validation record
	 *
	 * @param validationResult {@link ValidationResult} to store
	 */
	public void update(ValidationResult validationResult) {
		validationCache.update(key, validationResult);
	}

	/**
	 * Sets the validation error
	 *
	 * @param e {@link Exception}
	 */
	public void validationError(Exception e) {
		validationCache.error(key, e);
	}
	
	/**
	 * Checks if the entry must be deleted from the file cache (download cache)
	 *
	 * @return TRUE if the entry need to be deleted, FALSE otherwise
	 */
	public boolean isFileNeedToBeDeleted() {
		return downloadCache.isToBeDeleted(key);
	}
	
	/**
	 * Removes the entry from downloadCache if its value is TO_BE_DELETED
	 */
	public void deleteDownloadCacheIfNeeded() {
		if (downloadCache.isToBeDeleted(key)) {
			downloadCache.remove(key);
		}
	}
	
	/**
	 * Removes the entry from parsingCache if its value is TO_BE_DELETED
	 */
	public void deleteParsingCacheIfNeeded() {
		if (parsingCache.isToBeDeleted(key)) {
			parsingCache.remove(key);
		}
	}
	
	/**
	 * Removes the entry from parsingCache if its value is TO_BE_DELETED
	 */
	public void deleteValidationCacheIfNeeded() {
		if (validationCache.isToBeDeleted(key)) {
			validationCache.remove(key);
		}
	}

}
