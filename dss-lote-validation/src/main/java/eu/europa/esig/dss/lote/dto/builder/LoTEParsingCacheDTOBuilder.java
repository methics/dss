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
package eu.europa.esig.dss.lote.dto.builder;

import eu.europa.esig.dss.enumerations.ListType;
import eu.europa.esig.dss.lote.dto.LoTEParsingCacheDTO;
import eu.europa.esig.dss.lote.parsing.AbstractLoTEParsingResult;
import eu.europa.esig.dss.lote.parsing.LoLoTEParsingResult;
import eu.europa.esig.dss.model.lote.OtherListPointer;
import eu.europa.esig.dss.model.lote.TrustedEntity;
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
 * Builds {@code ParsingCacheDTO}
 *
 */
public class LoTEParsingCacheDTOBuilder extends AbstractParsingCacheDTOBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(LoTEParsingCacheDTOBuilder.class);

	/**
	 * Default constructor
	 *
	 * @param cachedEntry parsing cache entry
	 */
	public LoTEParsingCacheDTOBuilder(final CachedEntry<ParsingResult> cachedEntry) {
		super(cachedEntry);
	}

	@Override
	public LoTEParsingCacheDTO build() {
		return (LoTEParsingCacheDTO) super.build();
	}

	@Override
	protected LoTEParsingCacheDTO init(AbstractCacheDTO abstractCacheDTO) {
		return new LoTEParsingCacheDTO(abstractCacheDTO);
	}
	
	@Override
	public void build(AbstractParsingCacheDTO parsingCacheDTO) {
		super.build(parsingCacheDTO);

		LoTEParsingCacheDTO loteParsingCacheDTO = (LoTEParsingCacheDTO) parsingCacheDTO;
		if (isResultExist()) {
			loteParsingCacheDTO.setType(getType());
			loteParsingCacheDTO.setSequenceNumber(getSequenceNumber());
			loteParsingCacheDTO.setVersion(getVersion());
			loteParsingCacheDTO.setTerritory(getTerritory());
			loteParsingCacheDTO.setIssueDate(getIssueDate());
			loteParsingCacheDTO.setNextUpdateDate(getNextUpdateDate());
			loteParsingCacheDTO.setDistributionPoints(getDistributionPoints());
			loteParsingCacheDTO.setCurrentListPointers(getCurrentListPointers());
			loteParsingCacheDTO.setOtherListPointers(getOtherListPointers());
			loteParsingCacheDTO.setTrustedEntities(getTrustedEntities());
		}
	}

	private ListType getType() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getType();
		}
		LOG.debug("Cannot extract type for the entry. The parsed file is not a LoTE. Return empty list.");
		return null;
	}
	
	private Integer getSequenceNumber() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getSequenceNumber();
		}
		LOG.debug("Cannot extract sequenceNumber for the entry. The parsed file is not a LoTE. Return empty list.");
		return null;
	}
	
	private Integer getVersion() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getVersion();
		}
		LOG.debug("Cannot extract version for the entry. The parsed file is not a LoTE. Return empty list.");
		return null;
	}
	
	private String getTerritory() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getTerritory();
		}
		LOG.debug("Cannot extract territory for the entry. The parsed file is not a LoTE. Return empty list.");
		return null;
	}
	
	private Date getIssueDate() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getIssueDate();
		}
		LOG.debug("Cannot extract issueDate for the entry. The parsed file is not a LoTE. Return empty list.");
		return null;
	}
	
	private Date getNextUpdateDate() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getNextUpdateDate();
		}
		LOG.debug("Cannot extract nextUpdateDate for the entry. The parsed file is not a LoTE. Return empty list.");
		return null;
	}
	
	private List<String> getDistributionPoints() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getDistributionPoints();
		}
		LOG.debug("Cannot extract distributionPoints for the entry. The parsed file is not a LoTE. Return empty list.");
		return Collections.emptyList();
	}
	
	private List<TrustedEntity> getTrustedEntities() {
		ParsingResult result = getResult();
		if (result instanceof AbstractLoTEParsingResult) {
			return ((AbstractLoTEParsingResult) result).getTrustedEntities();
		}
		LOG.debug("Cannot extract trustedEntities for the entry. The parsed file is not a LoTE. Return empty list.");
		return Collections.emptyList();
	}
	
	private List<OtherListPointer> getCurrentListPointers() {
		ParsingResult result = getResult();
		if (result instanceof LoLoTEParsingResult) {
			return ((LoLoTEParsingResult) result).getCurrentListPointers();
		}
		LOG.debug("Cannot extract current list pointers for the entry. The parsed file is not a LoLoTE. Return empty list.");
		return Collections.emptyList();
	}
	
	private List<OtherListPointer> getOtherListPointers() {
		ParsingResult result = getResult();
		if (result instanceof LoLoTEParsingResult) {
			return ((LoLoTEParsingResult) result).getOtherListPointers();
		}
		LOG.debug("Cannot extract other list pointers for the entry. The parsed file is not a LoLoTE. Return empty list.");
		return Collections.emptyList();
	}

}
