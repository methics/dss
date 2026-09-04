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

/**
 * Parametrized interface to access cache in read-only mode
 *
 * @param <D> {@link DownloadInfoRecord}
 * @param <P> {@link ParsingInfoRecord}
 * @param <V> {@link ValidationInfoRecord}
 */
public interface ParametrizedReadOnlyCacheAccess<D extends DownloadInfoRecord, P extends ParsingInfoRecord, V extends ValidationInfoRecord> extends ReadOnlyCacheAccess {

    /**
     * Returns download cache DTO result
     *
     * @param key {@link CacheKey} to extract download result for
     * @return {@link DownloadInfoRecord}
     */
    D getDownloadInfoRecord(final CacheKey key);

    /**
     * Returns download cache DTO result
     *
     * @param key {@link CacheKey} to extract parsing result for
     * @return {@link ParsingInfoRecord}
     */
    P getParsingInfoRecord(final CacheKey key);

    /**
     * Returns download cache DTO result
     *
     * @param key {@link CacheKey} to extract validation result for
     * @return {@link ValidationInfoRecord}
     */
    V getValidationInfoRecord(final CacheKey key);

}
