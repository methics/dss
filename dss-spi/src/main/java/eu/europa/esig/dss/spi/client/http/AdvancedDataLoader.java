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
package eu.europa.esig.dss.spi.client.http;

import eu.europa.esig.dss.model.http.ResponseEnvelope;

/**
 * This data loader is used to perform a remote request (HTTP, HTTPS, etc.) and
 * retrieve a {@code eu.europa.esig.dss.model.http.ResponseEnvelope} object,
 * containing contextual information delivered from the response (response body, headers, session data, etc.).
 *
 */
public interface AdvancedDataLoader extends DataLoader {

    /**
     * Executes a GET request and returns an {@code ResponseEnvelope} object.
     * This method included the response message body, context and metadata within the response object.
     *
     * @param url {@link String} URL to perform request to
     * @return {@link ResponseEnvelope}
     */
    ResponseEnvelope requestGet(String url);

    /**
     * Executes a GET request and returns an {@code ResponseEnvelope} object.
     * This method allows configuration whether the response context (HTTP headers, TLS/SSL certificates, etc.),
     * is to be included within the response object.
     * The response body will be included within the response, when calling this method.
     *
     * @param url {@link String} URL to perform request to
     * @param includeResponseDetails defines whether the response context (HTTP headers, metadata) is to be included within the response
     * @return {@link ResponseEnvelope}
     */
    ResponseEnvelope requestGet(String url, boolean includeResponseDetails);

    /**
     * Executes a GET request and returns an {@code ResponseEnvelope} object.
     * This method allows configuration whether the response context (HTTP headers, TLS/SSL certificates, etc.),
     * as well as the response message body are to be included within the response object.
     * The data which is not included, won't be read by the execution process, thus helping to achieve time-memory efficiency.
     *
     * @param url {@link String} URL to perform request to
     * @param includeResponseDetails defines whether the response context (HTTP headers, metadata) is to be included within the response
     * @param includeResponseBody defines whether the response body is to be included within the response
     * @return {@link ResponseEnvelope}
     */
    ResponseEnvelope requestGet(String url, boolean includeResponseDetails, boolean includeResponseBody);

    /**
     * Executes a POST request and returns an {@code ResponseEnvelope} object.
     * This method included the response message body, context and metadata within the response object.
     *
     * @param url {@link String} URL to perform request to
     * @param content byte array containing request content for the POST call
     * @return {@link ResponseEnvelope}
     */
    ResponseEnvelope requestPost(String url, byte[] content);

    /**
     * Executes a POST request and returns an {@code ResponseEnvelope} object.
     * This method allows configuration whether the response context (HTTP headers, TLS/SSL certificates, etc.),
     * is to be included within the response object.
     * The response body will be included within the response, when calling this method.
     *
     * @param url {@link String} URL to perform request to
     * @param content byte array containing request content for the POST call
     * @param includeResponseDetails defines whether the response context (HTTP headers, metadata) is to be included within the response
     * @return {@link ResponseEnvelope}
     */
    ResponseEnvelope requestPost(String url, byte[] content, boolean includeResponseDetails);

    /**
     * Executes a POST request and returns an {@code ResponseEnvelope} object.
     * This method allows configuration whether the response context (HTTP headers, TLS/SSL certificates, etc.),
     * as well as the response message body are to be included within the response object.
     * The data which is not included, won't be read by the execution process, thus helping to achieve time-memory efficiency.
     *
     * @param url {@link String} URL to perform request to
     * @param content byte array containing request content for the POST call
     * @param includeResponseDetails defines whether the response context (HTTP headers, metadata) is to be included within the response
     * @param includeResponseBody defines whether the response body is to be included within the response
     * @return {@link ResponseEnvelope}
     */
    ResponseEnvelope requestPost(String url, byte[] content, boolean includeResponseDetails, boolean includeResponseBody);

}
