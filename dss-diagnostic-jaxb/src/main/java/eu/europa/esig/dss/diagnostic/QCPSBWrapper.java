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
package eu.europa.esig.dss.diagnostic;

import eu.europa.esig.dss.diagnostic.jaxb.XmlQcPSB;

/**
 * The class provides a user-friendly API for dealing with {@code eu.europa.esig.dss.diagnostic.jaxb.XmlQcPSB}
 *
 */
public class QCPSBWrapper {

    /** Wrapped object */
    private final XmlQcPSB wrapped;

    /**
     * Default constructor
     *
     * @param xmlQcPSB {@link XmlQcPSB}
     */
    public QCPSBWrapper(XmlQcPSB xmlQcPSB) {
        this.wrapped = xmlQcPSB;
    }

    /**
     * Gets the two-letter code of the legislation country (ISO 3166 alpha-2 country codes or 'EU')
     *
     * @return {@link String}
     */
    public String getCountryOfLegislation() {
        return wrapped.getCountryOfLegislation();
    }

    /**
     * Gets the unique identification of authentic source
     *
     * @return {@link String}
     */
    public String getAuthSourceIdentification() {
        return wrapped.getAuthSourceIdentification();
    }

    /**
     * Gets the legislation identification
     *
     * @return {@link String}
     */
    public String getLegislationIdentification() {
        return wrapped.getLegislationIdentification();
    }

}
