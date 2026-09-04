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
package eu.europa.esig.dss.enumerations;

/**
 * The recommendation element shall be used to indicate that a mechanism and its parameters are either
 * Recommended (R) or Legacy (L), as defined in ETSI TS 119 312 [i.2], clause 3.1.
 *
 */
public enum CryptographicSuiteRecommendation {

    /** Recommended cryptographic algorithm */
    RECOMMENDED("R"),

    /** Legacy cryptographic algorithm */
    LEGACY("L");

    /** The string value identifying the recommendation type */
    private final String value;

    /**
     * Default constructor
     *
     * @param value {@link String}
     */
    CryptographicSuiteRecommendation(final String value) {
        this.value = value;
    }

    /**
     * Gets the value of the recommendation type
     *
     * @return {@link String}
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns a {@code CryptographicSuiteRecommendation} by the given value
     *
     * @param value {@link String} to get {@link CryptographicSuiteRecommendation} for
     * @return {@link CryptographicSuiteRecommendation}
     */
    public static CryptographicSuiteRecommendation fromValue(String value) {
        if (value != null) {
            for (CryptographicSuiteRecommendation recommendation : CryptographicSuiteRecommendation.values()) {
                if (recommendation.value.equals(value)) {
                    return recommendation;
                }
            }
        }
        return null;
    }

}
