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
package eu.europa.esig.dss.model.lote.record;

import eu.europa.esig.dss.enumerations.ListType;
import eu.europa.esig.dss.model.job.ParsingInfoRecord;
import eu.europa.esig.dss.model.lote.OtherListPointer;
import eu.europa.esig.dss.model.lote.TrustedEntity;

import java.util.Date;
import java.util.List;

/**
 * Represents a LoTE parsing result record
 */
public interface LoTEParsingInfoRecord extends ParsingInfoRecord {

	/**
	 * Gets the List Type
	 *
	 * @return {@link ListType}
	 */
	ListType getType();

	/**
	 * Gets the List sequence number
	 *
	 * @return {@link Integer}
	 */
	Integer getSequenceNumber();

	/**
	 * Gets List version
	 *
	 * @return {@link Integer}
	 */
	Integer getVersion();

	/**
	 * Gets the List territory (country)
	 *
	 * @return {@link String}
	 */
	String getTerritory();

	/**
	 * Gets issuing date
	 *
	 * @return {@link Date}
	 */
	Date getIssueDate();

	/**
	 * Gets next update date
	 *
	 * @return {@link Date}
	 */
	Date getNextUpdateDate();

	/**
	 * Gets distribution points
	 *
	 * @return a list of {@link String}s
	 */
	List<String> getDistributionPoints();

	/**
	 * Gets trusted entities
	 *
	 * @return a list of {@link TrustedEntity}s
	 */
	List<TrustedEntity> getTrustedEntities();

	/**
	 * Gets List of Lists other TSL pointers
	 *
	 * @return a list of {@link OtherListPointer}s
	 */
	List<OtherListPointer> getCurrentListPointers();

	/**
	 * Gets LIsts other TSL pointers
	 *
	 * @return a list of {@link OtherListPointer}s
	 */
	List<OtherListPointer> getOtherListPointers();

	/**
	 * Gets pivot URLs
	 *
	 * @return a list of {@link String}s
	 */
	List<String> getPivotUrls();

	/**
	 * Gets signing certificate announcement URL
	 *
	 * @return {@link String}
	 */
	String getSigningCertificateAnnouncementUrl();
	
	/**
	 * Returns a number of all trusted entities present in the List
	 *
	 * @return trusted entities number
	 */
	int getTrustedEntitiesNumber();
	
	/**
	 * Returns a number of all trusted services present in the List
	 *
	 * @return trusted services number
	 */
	int getTrustedServicesNumber();
	
	/**
	 * Returns a number of all {@code CertificateToken}s present in the List
	 *
	 * @return number of certificates
	 */
	int getCertNumber();

	/**
	 * Gets a list of error messages when occurred during the structure validation
	 *
	 * @return a list of {@link String} structure validation messages, empty list if the structure validation succeeded
	 */
	List<String> getStructureValidationMessages();
	
}
