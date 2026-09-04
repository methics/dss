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

/**
 * Factory used to create objects to interact with the cache
 *
 */
public interface CacheAccessFactory {

    /**
     * Loads a class to deal with a cache by the {@code key} records
     *
     * @param key {@link CacheKey} to use
     * @return {@link CacheAccessByKey}
     */
    CacheAccessByKey getCacheAccess(CacheKey key);

    /**
     * Loads a class for document updates
     *
     * @return {@link ChangesCacheAccess}
     */
    ChangesCacheAccess getDocumentChangesCacheAccess();

    /**
     * Loads a read-only cache access
     *
     * @return {@link ReadOnlyCacheAccess}
     */
    ReadOnlyCacheAccess getReadOnlyCacheAccess();

    /**
     * Loads a cache access to synchronize records
     *
     * @return {@link SynchronizerCacheAccess}
     */
    SynchronizerCacheAccess getSynchronizerCacheAccess();

    /**
     * Loads a cache access to load the information about the current cache state
     *
     * @return {@link DebugCacheAccess}
     */
    DebugCacheAccess getDebugCacheAccess();

}
