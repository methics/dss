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
package eu.europa.esig.dss.model.tls;

import eu.europa.esig.dss.model.x509.CertificateToken;

import java.util.List;

/**
 * This class represents information obtained from a remote server as the result of the TLS/SSL handshake.
 *
 */
public class TLSCertificates {

    /** Collection of a certificate tokens returned after TLS/SSL handshake */
    private List<CertificateToken> certificates;

    /** The value of the "Link" response header with a rel value of tls-certificate-binding */
    private String tlsCertificateBindingUrl;

    /**
     * Default constructor
     */
    public TLSCertificates() {
        // empty
    }

    /**
     * Gets a list of certificates returned by a remote server during the TLS/SSL handshake
     *
     * @return a list of {@link CertificateToken}s
     */
    public List<CertificateToken> getCertificates() {
        return certificates;
    }

    /**
     * Sets a list of certificates returned by a remote server during the TLS/SSL handshake
     *
     * @param certificates a list of {@link CertificateToken}s
     */
    public void setCertificates(List<CertificateToken> certificates) {
        this.certificates = certificates;
    }

    /**
     * Gets value of the "Link" response header with a rel value of tls-certificate-binding.
     * This URL is used to extract a TLS/SSL binding signature.
     *
     * @return {@link String}
     */
    public String getTLSCertificateBindingUrl() {
        return tlsCertificateBindingUrl;
    }

    /**
     * Sets value of the "Link" response header with a rel value of tls-certificate-binding.
     * This URL is used to extract a TLS/SSL binding signature.
     *
     * @param tlsCertificateBindingUrl {@link String}
     */
    public void setTLSCertificateBindingUrl(String tlsCertificateBindingUrl) {
        this.tlsCertificateBindingUrl = tlsCertificateBindingUrl;
    }

}
