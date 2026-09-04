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

import java.util.Set;

/**
 * Access cache in read-only mode
 *
 */
public interface ReadOnlyCacheAccess {

    /**
     * Returns download cache DTO result
     *
     * @param key {@link CacheKey} to extract download result for
     * @return {@link DownloadInfoRecord}
     */
    DownloadInfoRecord getDownloadInfoRecord(final CacheKey key);

    /**
     * Returns download cache DTO result
     *
     * @param key {@link CacheKey} to extract parsing result for
     * @return {@link ParsingInfoRecord}
     */
    ParsingInfoRecord getParsingInfoRecord(final CacheKey key);

    /**
     * Returns download cache DTO result
     *
     * @param key {@link CacheKey} to extract validation result for
     * @return {@link ValidationInfoRecord}
     */
    ValidationInfoRecord getValidationInfoRecord(final CacheKey key);

    /**
     * This method returns all found keys in any cache
     *
     * @return a set of cache keys
     */
    Set<CacheKey> getAllCacheKeys();

}
