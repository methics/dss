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
package eu.europa.esig.dss.tsl.runnable;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.tsl.OtherTSLPointer;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.tsl.cache.access.TLCacheAccessByKey;
import eu.europa.esig.dss.tsl.dto.TLParsingCacheDTO;
import eu.europa.esig.dss.tsl.parsing.ParsingUtils;
import eu.europa.esig.dss.validation.job.cache.access.AbstractCacheAccessByKey;

/**
 * This class creates an instance of {@code eu.europa.esig.dss.tsl.runnable.PivotProcessingResult}
 * from a given {@code CacheAccessByKey}
 *
 */
public class PivotProcessingResultFromCacheAccessBuilder {

    /** Cache access to the given pivot */
    private final TLCacheAccessByKey cacheAccessByKey;

    /**
     * Default constructor
     *
     * @param cacheAccessByKey {@link AbstractCacheAccessByKey}
     */
    public PivotProcessingResultFromCacheAccessBuilder(final TLCacheAccessByKey cacheAccessByKey) {
        this.cacheAccessByKey = cacheAccessByKey;
    }

    /**
     * Builds the {@code PivotProcessingResult}
     *
     * @return {@link PivotProcessingResult}
     */
    public PivotProcessingResult build() {
        TLParsingCacheDTO parsingCacheEntry = cacheAccessByKey.getParsingReadOnlyResult();
        OtherTSLPointer xmlLotlPointer = ParsingUtils.getXMLLOTLPointer(parsingCacheEntry);
        return new PivotProcessingResult(getDocument(), getCertificateSource(xmlLotlPointer), getLotlLocation(xmlLotlPointer));
    }

    private DSSDocument getDocument() {
        if (cacheAccessByKey.getDownloadReadOnlyResult() != null && cacheAccessByKey.getDownloadReadOnlyResult().isResultExist()) {
            return cacheAccessByKey.getDownloadReadOnlyResult().getDocument();
        }
        return null;
    }

    private CertificateSource getCertificateSource(OtherTSLPointer xmlLotlPointer) {
        if (xmlLotlPointer != null) {
            return ParsingUtils.getLOTLAnnouncedCertificateSource(xmlLotlPointer);
        }
        return null;
    }

    private String getLotlLocation(OtherTSLPointer xmlLotlPointer) {
        if (xmlLotlPointer != null) {
            return xmlLotlPointer.getTSLLocation();
        }
        return null;
    }

}
