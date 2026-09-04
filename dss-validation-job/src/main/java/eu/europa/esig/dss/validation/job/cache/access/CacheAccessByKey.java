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

import eu.europa.esig.dss.validation.job.cache.CacheKey;
import eu.europa.esig.dss.validation.job.download.DownloadResult;
import eu.europa.esig.dss.validation.job.parsing.ParsingResult;
import eu.europa.esig.dss.validation.job.validation.ValidationResult;

/**
 * Provides an interface for accessing a cache by key
 *
 */
public interface CacheAccessByKey extends ReadOnlyCacheAccessByKey {

    /**
     * Returns the CacheKey
     *
     * @return {@link CacheKey}
     */
    CacheKey getCacheKey();

    /**
     * Checks if the download result is up to date for the given key
     *
     * @param DownloadResult {@link DownloadResult}
     * @return TRUE if the download result matches, FALSE otherwise
     */
    boolean isUpToDate(DownloadResult DownloadResult);

    /**
     * Updates the download result
     *
     * @param result {@link DownloadResult} to store
     */
    void update(DownloadResult result);

    /**
     * Sets the download error
     *
     * @param e {@link Exception}
     */
    void downloadError(Exception e);

    /**
     * Gets of the parsing refresh is needed
     *
     * @return TRUE if the parsing refresh is needed, FALSE otherwise
     */
    boolean isParsingRefreshNeeded();

    /**
     * Updates the parsing result
     *
     * @param parsingResult {@link ParsingResult} to store
     */
    void update(ParsingResult parsingResult);

    /**
     * Sets the parsing record to the expired state
     */
    void expireParsing();

    /**
     * Sets the parsing error
     *
     * @param e {@link Exception}
     */
    void parsingError(Exception e);

    /**
     * Gets if the validation refresh is needed
     *
     * @return TRUE if the validation refresh is needed, FALSE otherwise
     */
    boolean isValidationRefreshNeeded();

    /**
     * Expires the validation record
     */
    void expireValidation();

    /**
     * Updates the validation record
     *
     * @param validationResult {@link ValidationResult} to store
     */
    void update(ValidationResult validationResult);

    /**
     * Sets the validation error
     *
     * @param e {@link Exception}
     */
    void validationError(Exception e);

    /**
     * Checks if the entry must be deleted from the file cache (download cache)
     *
     * @return TRUE if the entry need to be deleted, FALSE otherwise
     */
    boolean isFileNeedToBeDeleted();

    /**
     * Removes the entry from downloadCache if its value is TO_BE_DELETED
     */
    void deleteDownloadCacheIfNeeded();

    /**
     * Removes the entry from parsingCache if its value is TO_BE_DELETED
     */
    void deleteParsingCacheIfNeeded();

    /**
     * Removes the entry from parsingCache if its value is TO_BE_DELETED
     */
    void deleteValidationCacheIfNeeded();
    
}
