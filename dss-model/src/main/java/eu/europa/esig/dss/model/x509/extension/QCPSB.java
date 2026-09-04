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
package eu.europa.esig.dss.model.x509.extension;

import java.io.Serializable;

/**
 * Defines a Public Sector Body's Electronic Attestation of Attributes (PSBEAA) provider certificate
 *
 */
public class QCPSB implements Serializable {

    private static final long serialVersionUID = 5281169017459597208L;

    /**
     * countryOfLegislation PrintableString (SIZE (2))
     * (CONSTRAINED BY { -- ISO 3166 alpha-2 country codes or 'EU' -- }),
     *  -- this field shall contain the alpha-2 country code of the legislation framework of public
     * sector body
     *  -- In the case of European Union law 'EU' shall be used in place of the country code
     */
    private String countryOfLegislation;

    /**
     * authSourceIdentification UTF8String,
     -- this field is for the unique identification of authentic source
     */
    private String authSourceIdentification;

    /**
     * legislationIdentification UTF8String
     */
    private String legislationIdentification;

    /**
     * Default constructor
     */
    public QCPSB() {
        // empty
    }

    /**
     * Gets the country of legislation.
     * The value shall be represented by a two-letter ISO 3166 alpha-2 country code.
     *
     * @return {@link String}
     */
    public String getCountryOfLegislation() {
        return countryOfLegislation;
    }

    /**
     * Sets the country of legislation.
     * The value shall be represented by a two-letter ISO 3166 alpha-2 country code.
     *
     * @param countryOfLegislation {@link String}
     */
    public void setCountryOfLegislation(String countryOfLegislation) {
        this.countryOfLegislation = countryOfLegislation;
    }

    /**
     * Gets the authentic source identification
     *
     * @return {@link String}
     */
    public String getAuthSourceIdentification() {
        return authSourceIdentification;
    }

    /**
     * Sets the authentic source identification
     *
     * @param authSourceIdentification {@link String}
     */
    public void setAuthSourceIdentification(String authSourceIdentification) {
        this.authSourceIdentification = authSourceIdentification;
    }

    /**
     * Gets the legislation identification
     *
     * @return {@link String}
     */
    public String getLegislationIdentification() {
        return legislationIdentification;
    }

    /**
     * Sets the legislation identification
     *
     * @param legislationIdentification {@link String}
     */
    public void setLegislationIdentification(String legislationIdentification) {
        this.legislationIdentification = legislationIdentification;
    }

}
