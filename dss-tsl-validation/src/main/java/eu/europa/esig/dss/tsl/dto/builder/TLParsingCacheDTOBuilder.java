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
package eu.europa.esig.dss.tsl.dto.builder;

import eu.europa.esig.dss.enumerations.TSLType;
import eu.europa.esig.dss.model.tsl.OtherTSLPointer;
import eu.europa.esig.dss.model.tsl.TrustServiceProvider;
import eu.europa.esig.dss.tsl.dto.TLParsingCacheDTO;
import eu.europa.esig.dss.tsl.parsing.AbstractTLParsingResult;
import eu.europa.esig.dss.tsl.parsing.LOTLParsingResult;
import eu.europa.esig.dss.tsl.parsing.TLParsingResult;
import eu.europa.esig.dss.validation.job.cache.state.CachedEntry;
import eu.europa.esig.dss.validation.job.dto.AbstractCacheDTO;
import eu.europa.esig.dss.validation.job.dto.AbstractParsingCacheDTO;
import eu.europa.esig.dss.validation.job.dto.builder.AbstractParsingCacheDTOBuilder;
import eu.europa.esig.dss.validation.job.parsing.ParsingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Builds a parsing cache DTO for a Trusted List processing
 *
 */
public class TLParsingCacheDTOBuilder extends AbstractParsingCacheDTOBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TLParsingCacheDTOBuilder.class);

    /**
     * Default constructor
     *
     * @param cachedEntry parsing cache entry
     */
    public TLParsingCacheDTOBuilder(final CachedEntry<ParsingResult> cachedEntry) {
        super(cachedEntry);
    }

    @Override
    public TLParsingCacheDTO build() {
        return (TLParsingCacheDTO) super.build();
    }

    @Override
    protected TLParsingCacheDTO init(AbstractCacheDTO abstractCacheDTO) {
        return new TLParsingCacheDTO(abstractCacheDTO);
    }

    @Override
    protected void build(AbstractParsingCacheDTO parsingCacheDTO) {
        super.build(parsingCacheDTO);

        TLParsingCacheDTO tlParsingCacheDTO = (TLParsingCacheDTO) parsingCacheDTO;
        tlParsingCacheDTO.setTSLType(getTSLType());
        tlParsingCacheDTO.setSequenceNumber(getSequenceNumber());
        tlParsingCacheDTO.setVersion(getVersion());
        tlParsingCacheDTO.setTerritory(getTerritory());
        tlParsingCacheDTO.setIssueDate(getIssueDate());
        tlParsingCacheDTO.setNextUpdateDate(getNextUpdateDate());
        tlParsingCacheDTO.setDistributionPoints(getDistributionPoints());
        if (isLOTL()) {
            tlParsingCacheDTO.setLotlOtherPointers(getLOTLOtherPointers());
            tlParsingCacheDTO.setTlOtherPointers(getTLOtherPointers());
            tlParsingCacheDTO.setPivotUrls(getPivotUrls());
            tlParsingCacheDTO.setSigningCertificateAnnouncementUrl(getSigningCertificateAnnouncementUrl());
        } else {
            tlParsingCacheDTO.setTrustServiceProviders(getTrustServiceProviders());
        }
    }

    private boolean isLOTL() {
        return getResult() instanceof LOTLParsingResult;
    }

    private TSLType getTSLType() {
        ParsingResult result = getResult();
        if (result instanceof AbstractTLParsingResult) {
            return ((AbstractTLParsingResult) getResult()).getTSLType();
        }
        LOG.debug("Cannot extract TSLType for the entry. The parsed file is not a TL. Return empty list.");
        return null;
    }

    private Integer getSequenceNumber() {
        ParsingResult result = getResult();
        if (result instanceof AbstractTLParsingResult) {
            return ((AbstractTLParsingResult) getResult()).getSequenceNumber();
        }
        LOG.debug("Cannot extract sequenceNumber for the entry. The parsed file is not a TL. Return empty list.");
        return null;
    }

    private Integer getVersion() {
        ParsingResult result = getResult();
        if (result instanceof AbstractTLParsingResult) {
            return ((AbstractTLParsingResult) getResult()).getVersion();
        }
        LOG.debug("Cannot extract version for the entry. The parsed file is not a TL. Return empty list.");
        return null;
    }

    private String getTerritory() {
        ParsingResult result = getResult();
        if (result instanceof AbstractTLParsingResult) {
            return ((AbstractTLParsingResult) getResult()).getTerritory();
        }
        LOG.debug("Cannot extract territory for the entry. The parsed file is not a TL. Return empty list.");
        return null;
    }

    private Date getIssueDate() {
        ParsingResult result = getResult();
        if (result instanceof AbstractTLParsingResult) {
            return ((AbstractTLParsingResult) getResult()).getIssueDate();
        }
        LOG.debug("Cannot extract issueDate for the entry. The parsed file is not a TL. Return empty list.");
        return null;
    }

    private Date getNextUpdateDate() {
        ParsingResult result = getResult();
        if (result instanceof AbstractTLParsingResult) {
            return ((AbstractTLParsingResult) getResult()).getNextUpdateDate();
        }
        LOG.debug("Cannot extract nextUpdateDate for the entry. The parsed file is not a TL. Return empty list.");
        return null;
    }

    private List<String> getDistributionPoints() {
        ParsingResult result = getResult();
        if (result instanceof AbstractTLParsingResult) {
            return ((AbstractTLParsingResult) getResult()).getDistributionPoints();
        }
        LOG.debug("Cannot extract distributionPoints for the entry. The parsed file is not a TL. Return empty list.");
        return null;
    }

    private List<TrustServiceProvider> getTrustServiceProviders() {
        ParsingResult result = getResult();
        if (result instanceof TLParsingResult) {
            return ((TLParsingResult) getResult()).getTrustServiceProviders();
        }
        LOG.debug("Cannot extract trustServiceProviders for the entry. The parsed file is not a TL. Return empty list.");
        return Collections.emptyList();
    }

    private List<OtherTSLPointer> getLOTLOtherPointers() {
        ParsingResult result = getResult();
        if (result instanceof LOTLParsingResult) {
            return ((LOTLParsingResult) getResult()).getLotlPointers();
        }
        LOG.debug("Cannot extract LOTL other Pointers for the entry. The parsed file is not a LOTL. Return empty list.");
        return Collections.emptyList();
    }

    private List<OtherTSLPointer> getTLOtherPointers() {
        ParsingResult result = getResult();
        if (result instanceof LOTLParsingResult) {
            return ((LOTLParsingResult) getResult()).getTlPointers();
        }
        LOG.debug("Cannot extract TL other Pointers for the entry. The parsed file is not a LOTL. Return empty list.");
        return Collections.emptyList();
    }

    private List<String> getPivotUrls() {
        ParsingResult result = getResult();
        if (result instanceof LOTLParsingResult) {
            return ((LOTLParsingResult) getResult()).getPivotURLs();
        }
        LOG.debug("Cannot extract Pivot URLs for the entry. The parsed file is not a LOTL. Return empty list.");
        return Collections.emptyList();
    }

    private String getSigningCertificateAnnouncementUrl() {
        ParsingResult result = getResult();
        if (result instanceof LOTLParsingResult) {
            return ((LOTLParsingResult) getResult()).getSigningCertificateAnnouncementURL();
        }
        LOG.debug("Cannot extract Signing Certificate Announcement URL for the entry. The parsed file is not a LOTL. Return null.");
        return null;
    }

}
