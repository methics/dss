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
package eu.europa.esig.dss.tsl.cache.access;

import eu.europa.esig.dss.tsl.dto.TLParsingCacheDTO;
import eu.europa.esig.dss.tsl.job.TLReadOnlyCacheAccess;
import eu.europa.esig.dss.validation.job.cache.CacheKey;
import eu.europa.esig.dss.validation.job.cache.DownloadCache;
import eu.europa.esig.dss.validation.job.cache.ParsingCache;
import eu.europa.esig.dss.validation.job.cache.ValidationCache;
import eu.europa.esig.dss.validation.job.cache.access.AbstractCacheAccessByKey;
import eu.europa.esig.dss.validation.job.dto.DownloadCacheDTO;
import eu.europa.esig.dss.validation.job.dto.ValidationCacheDTO;

/**
 * Accesses cache information for a Trusted List by key
 *
 */
public class TLCacheAccessByKey extends AbstractCacheAccessByKey<DownloadCacheDTO, TLParsingCacheDTO, ValidationCacheDTO> {

    /**
     * Default constructor
     *
     * @param key                 {@link CacheKey} to use
     * @param downloadCache       {@link DownloadCache}
     * @param parsingCache        {@link ParsingCache}
     * @param validationCache     {@link ValidationCache}
     */
    public TLCacheAccessByKey(CacheKey key, DownloadCache downloadCache, ParsingCache parsingCache, ValidationCache validationCache) {
        super(key, downloadCache, parsingCache, validationCache, new TLReadOnlyCacheAccess(downloadCache, parsingCache, validationCache));
    }

}
