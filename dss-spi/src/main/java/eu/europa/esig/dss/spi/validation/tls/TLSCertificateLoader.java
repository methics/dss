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
package eu.europa.esig.dss.spi.validation.tls;

import eu.europa.esig.dss.model.tls.TLSCertificates;

import java.io.Serializable;

/**
 * The data loader which includes server webpage certificates to the response context.
 * Use the method {@code #getCertificates(url)} to extract the data.
 *
 */
public interface TLSCertificateLoader extends Serializable {

    /**
     * The method to extract TLS/SSL-certificates from the given web page
     *
     * @param urlString {@link String} representing a URL of a webpage with a secure connection (HTTPS)
     * @return {@link TLSCertificates} containing the chain of the TLS/SSL certificates and other supportive information
     */
    TLSCertificates getTLSCertificates(final String urlString);

}
