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
package eu.europa.esig.dss.diagnostic.claim;

import eu.europa.esig.dss.diagnostic.jaxb.XmlClaim;
import eu.europa.esig.dss.diagnostic.jaxb.XmlIntegrityClaim;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;

/**
 * Represents an integrity claim for a certain claim attribute
 *
 */
public class IntegrityClaimWrapper extends ClaimWrapper {

    /**
     * Default constructor
     *
     * @param wrapped {@link XmlClaim}
     */
    public IntegrityClaimWrapper(final XmlIntegrityClaim wrapped) {
        super(wrapped);
    }

    /**
     * Constructor with a parent claim provided
     *
     * @param wrapped {@link XmlClaim}
     * @param parent {@link ClaimWrapper}
     */
    public IntegrityClaimWrapper(final XmlIntegrityClaim wrapped, final ClaimWrapper parent) {
        super(wrapped, parent);
    }

    /**
     * Gets the digest algorithm used for the claim hash computation
     *
     * @return {@link DigestAlgorithm}
     */
    public DigestAlgorithm getDigestAlgorithm() {
        return getWrapped().getDigestMethod();
    }

    /**
     * Gets the digest value of the computed claim integrity hash
     *
     * @return byte array
     */
    public byte[] getDigestValue() {
        return getWrapped().getDigestValue();
    }

    @Override
    public XmlIntegrityClaim getWrapped() {
        return (XmlIntegrityClaim) super.getWrapped();
    }

}
