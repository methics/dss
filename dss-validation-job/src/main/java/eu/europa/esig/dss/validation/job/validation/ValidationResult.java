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
package eu.europa.esig.dss.validation.job.validation;

import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.validation.job.cache.CachedResult;

import java.util.Date;
import java.util.List;

/**
 * Provides an interface for extraction of information about validation task result
 *
 */
public interface ValidationResult extends CachedResult {

    /**
     * Gets validation Indication
     *
     * @return {@link Indication}
     */
    Indication getIndication();

    /**
     * Gets validation SubIndication
     *
     * @return {@link SubIndication}
     */
    SubIndication getSubIndication();

    /**
     * Gets the (claimed) signing time
     *
     * @return {@link Date}
     */
    Date getSigningTime();

    /**
     * Gets the signing certificate
     *
     * @return {@link CertificateToken}
     */
    CertificateToken getSigningCertificate();

    /**
     * Gets a list of signing candidates
     *
     * @return a list of {@link CertificateToken}s
     */
    List<CertificateToken> getPotentialSigners();

}
