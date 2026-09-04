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
package eu.europa.esig.dss.xades.definition.tsl;

import eu.europa.esig.dss.xml.common.definition.AbstractPath;
import eu.europa.esig.dss.xml.common.xpath.XPathQuery;

/**
 * ETSI TS 119 612 Trusted List XML path definitions
 *
 */
public class TrustedListPath extends AbstractPath {

    private static final long serialVersionUID = 6983466430849016304L;

    /** The path to reach a tl:AdditionalServiceInformation element */
    public static final XPathQuery ADDITIONAL_SERVICE_INFORMATION_PATH =  fromCurrentPosition(TrustedListElement.ADDITIONAL_SERVICE_INFORMATION);

    /** The path to reach a tl:NextUpdate element */
    public static final XPathQuery NEXT_UPDATE_PATH =  fromCurrentPosition(TrustedListElement.SCHEME_INFORMATION, TrustedListElement.NEXT_UPDATE);

    /** The path to reach a tl:OtherTLSPoiner element */
    public static final XPathQuery OTHER_TSL_POINTER_PATH = fromCurrentPosition(TrustedListElement.SCHEME_INFORMATION,
            TrustedListElement.POINTERS_TO_OTHER_TSL, TrustedListElement.OTHER_TSL_POINTER);

    /** The path to reach a tl:ServiceDigitalIdentity element */
    public static final XPathQuery SERVICE_DIGITAL_IDENTITY_PATH = fromCurrentPosition(TrustedListElement.SERVICE_DIGITAL_IDENTITY);

    /** The path to reach a tl:TSLVersionIdentifier element */
    public static final XPathQuery TSL_VERSION_IDENTIFIER_PATH = fromCurrentPosition(TrustedListElement.SCHEME_INFORMATION, TrustedListElement.TSL_VERSION_IDENTIFIER);

    /** The path to reach a tl:X509Certificate element */
    public static final XPathQuery X509_CERTIFICATE_PATH = fromCurrentPosition(TrustedListElement.SERVICE_DIGITAL_IDENTITIES,
            TrustedListElement.SERVICE_DIGITAL_IDENTITY, TrustedListElement.DIGITAL_ID, TrustedListElement.X509_CERTIFICATE);

    /** The path to reach a tl:ServiceInformation element */
    public static final XPathQuery TSP_SERVICE_INFORMATION_PATH = fromCurrentPosition(TrustedListElement.TRUST_SERVICE_PROVIDER_LIST,
            TrustedListElement.TRUST_SERVICE_PROVIDER, TrustedListElement.TSP_SERVICES, TrustedListElement.TSP_SERVICE, TrustedListElement.SERVICE_INFORMATION);

    /**
     * Default constructor
     */
    public TrustedListPath() {
        // empty
    }

}
