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
package eu.europa.esig.dss.lote.job;

import eu.europa.esig.dss.lote.dto.LoTEParsingCacheDTO;
import eu.europa.esig.dss.lote.source.LoLoTESource;
import eu.europa.esig.dss.lote.source.LoTESource;
import eu.europa.esig.dss.model.lote.OtherListPointer;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.job.cache.CacheKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class creates a list of {@link LoTESource}s
 *
 */
public class LoTESourceBuilder {

    /** The list of original List sources */
    private final List<LoLoTESource> originalLoLoTESources;

    /** The parsing results map */
    private final Map<CacheKey, LoTEParsingCacheDTO> parsingResults;

    /**
     * Default constructor
     *
     * @param originalLoTESources a list of original {@link LoTESource}s, to get other {@link LoTESource}s from, if any
     * @param parsingResults a map of parsing results
     */
    public LoTESourceBuilder(List<LoLoTESource> originalLoTESources, Map<CacheKey, LoTEParsingCacheDTO> parsingResults) {
        this.originalLoLoTESources = originalLoTESources;
        this.parsingResults = parsingResults;
    }

    /**
     * Builds a list of {@code ListSource}s
     *
     * @return a list of {@link LoTESource}s
     */
    public List<LoTESource> build() {
        List<LoTESource> result = new ArrayList<>();
        if (originalLoLoTESources != null) {
            for (LoTESource loTESource : originalLoLoTESources) {
                LoTEParsingCacheDTO cachedResult = parsingResults.get(loTESource.getCacheKey());
                if (cachedResult != null && cachedResult.isResultExist()) {
                    List<OtherListPointer> otherListPointers = cachedResult.getOtherListPointers();
                    if (Utils.isCollectionNotEmpty(otherListPointers)) {
                        for (OtherListPointer otherListPointer : otherListPointers) {
                            result.add(getListSource(otherListPointer, loTESource));
                        }
                    }
                }
            }
        }
        return result;
    }

    private LoTESource getListSource(OtherListPointer otherListPointer, LoTESource parentLoTESource) {
        LoTESource loTESource = new LoTESource();
        loTESource.setUrl(otherListPointer.getLocationUrl());
        loTESource.setCertificateSource(getCertificateSource(otherListPointer.getSdiCertificates()));
        loTESource.setTrustedEntityPredicate(parentLoTESource.getTrustedEntityPredicate());
        loTESource.setTrustedServicePredicate(parentLoTESource.getTrustedServicePredicate());
        // TODO : add recursive List handling ?
        // TODO : add supported versions ?
        return loTESource;
    }

    private CertificateSource getCertificateSource(List<CertificateToken> certificates) {
        CertificateSource certificateSource = new CommonCertificateSource();
        for (CertificateToken certificate : certificates) {
            certificateSource.addCertificate(certificate);
        }
        return certificateSource;
    }

}
