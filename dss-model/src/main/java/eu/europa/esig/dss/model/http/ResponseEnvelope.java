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
package eu.europa.esig.dss.model.http;

import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains information retrieved from a Response (HTTP, HTTPS, etc.)
 *
 */
public class ResponseEnvelope {

    /** Contains the response body binaries obtained from a remote server */
    private byte[] responseBody;

    /** Map of headers returned by the HTTP Response */
    private Map<String, List<String>> headers = new HashMap<>();

    /** TLS/SSL certificates used to establish a secure connection, when applicable (for HTTPS calls) */
    private Certificate[] tlsCertificates;

    /**
     * Default constructor
     */
    public ResponseEnvelope() {
        // empty
    }

    /**
     * Constructor with response message body provided
     *
     * @param responseBody byte array containing the message body
     */
    public ResponseEnvelope(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Gets the response message body
     *
     * @return byte array
     */
    public byte[] getResponseBody() {
        return responseBody;
    }

    /**
     * Sets the response message body
     *
     * @param responseBody byte array
     */
    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Gets the response headers (e.g. HTTP(S) headers)
     *
     * @return a map of {@link String} header names and their values
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Sets the response headers (e.g. HTTP(S) headers)
     *
     * @param headers a map of {@link String} header names and their values
     */
    public void setHeaders(Map<String, List<String>> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    /**
     * Gets the TLS/SSL certificates used by the remote server to establish a secure connection (e.g. for HTTPS)
     *
     * @return array of {@code Certificate}s
     */
    public Certificate[] getTLSCertificates() {
        return tlsCertificates;
    }

    /**
     * Sets the TLS/SSL certificates used by the remote server to establish a secure connection (e.g. for HTTPS)
     *
     * @param tlsCertificates array of {@code Certificate}s
     */
    public void setTLSCertificates(Certificate[] tlsCertificates) {
        this.tlsCertificates = tlsCertificates;
    }

}
