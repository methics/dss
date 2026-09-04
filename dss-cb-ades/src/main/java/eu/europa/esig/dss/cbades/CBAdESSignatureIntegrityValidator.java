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
package eu.europa.esig.dss.cbades;

import eu.europa.esig.dss.cbades.validation.CBORSignature;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.x509.SignatureIntegrityValidator;

import java.security.PublicKey;

/**
 * Checks the integrity of a COSE/CB-AdES SignatureValue
 *
 */
public class CBAdESSignatureIntegrityValidator extends SignatureIntegrityValidator {

    /** The COSE signature to validate */
    private final CBORSignature cose;

    /**
     * Default constructor
     *
     * @param cose {@link CBORSignature}
     */
    public CBAdESSignatureIntegrityValidator(final CBORSignature cose) {
        this.cose = cose;
    }

    @Override
    protected boolean verify(PublicKey publicKey) throws DSSException {
        try {
            cose.setKey(publicKey);
            return cose.verifySignature();
        } catch (Exception e) {
            throw new DSSException(e);
        }
    }

}
