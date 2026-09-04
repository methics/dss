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
package eu.europa.esig.dss.lote.cache.access;

import eu.europa.esig.dss.lote.dto.LoTEParsingCacheDTO;
import eu.europa.esig.dss.lote.job.LoTEReadOnlyCacheAccess;
import eu.europa.esig.dss.validation.job.cache.CacheKey;
import eu.europa.esig.dss.validation.job.cache.access.AbstractCacheAccessFactory;
import eu.europa.esig.dss.validation.job.dto.DownloadCacheDTO;
import eu.europa.esig.dss.validation.job.dto.ValidationCacheDTO;

/**
 * Accesses the cache for Trusted Lists validation job
 *
 */
public class LoTECacheAccessFactory extends AbstractCacheAccessFactory<DownloadCacheDTO, LoTEParsingCacheDTO, ValidationCacheDTO> {

    /**
     * Default constructor
     */
    public LoTECacheAccessFactory() {
        // empty
    }

    @Override
    public LoTECacheAccessByKey getCacheAccess(CacheKey key) {
        return new LoTECacheAccessByKey(key, downloadCache, parsingCache, validationCache);
    }

    @Override
    public LoTEReadOnlyCacheAccess getReadOnlyCacheAccess() {
        return new LoTEReadOnlyCacheAccess(downloadCache, parsingCache, validationCache);
    }

}
