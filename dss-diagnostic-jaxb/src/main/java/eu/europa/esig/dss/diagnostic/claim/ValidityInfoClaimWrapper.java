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
import eu.europa.esig.dss.diagnostic.jaxb.XmlValidityInfoClaim;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an {@code eu.europa.esig.dss.diagnostic.jaxb.XmlValidityInfoClaim}
 *
 */
public class ValidityInfoClaimWrapper extends ClaimWrapper {

    /**
     * Default constructor
     *
     * @param wrapped {@link XmlValidityInfoClaim}
     */
    public ValidityInfoClaimWrapper(final XmlValidityInfoClaim wrapped) {
        super(wrapped);
    }

    /**
     * Gets the timestamp at which the MSO signature was created
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getSigned() {
        XmlClaim signed = getWrapped().getSigned();
        if (signed != null) {
            return new ClaimWrapper(signed, this);
        }
        return null;
    }

    /**
     * Gets the timestamp before which the MSO is not yet valid
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getValidFrom() {
        XmlClaim validFrom = getWrapped().getValidFrom();
        if (validFrom != null) {
            return new ClaimWrapper(validFrom, this);
        }
        return null;
    }

    /**
     * Gets the timestamp after which the MSO is no longer valid
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getValidUntil() {
        XmlClaim validUntil = getWrapped().getValidUntil();
        if (validUntil != null) {
            return new ClaimWrapper(validUntil, this);
        }
        return null;
    }

    /**
     * Gets the timestamp at which the issuing authority infrastructure expects to re-sign the MSO
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getExpectedUpdate() {
        XmlClaim expectedUpdate = getWrapped().getExpectedUpdate();
        if (expectedUpdate != null) {
            return new ClaimWrapper(expectedUpdate, this);
        }
        return null;
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    public Map<String, ClaimWrapper> getMap() {
        final Map<String, ClaimWrapper> result = new HashMap<>(super.getMap());
        ClaimWrapper signed = getSigned();
        if (signed != null) {
            result.put(signed.getName(), signed);
        }
        ClaimWrapper validFrom = getValidFrom();
        if (validFrom != null) {
            result.put(validFrom.getName(), validFrom);
        }
        ClaimWrapper validUntil = getValidUntil();
        if (validUntil != null) {
            result.put(validUntil.getName(), validUntil);
        }
        ClaimWrapper expectedUpdate = getExpectedUpdate();
        if (expectedUpdate != null) {
            result.put(expectedUpdate.getName(), expectedUpdate);
        }
        return result;
    }

    @Override
    public XmlValidityInfoClaim getWrapped() {
        return (XmlValidityInfoClaim) super.getWrapped();
    }

}