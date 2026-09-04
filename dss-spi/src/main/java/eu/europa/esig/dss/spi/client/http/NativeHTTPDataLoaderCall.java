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
import eu.europa.esig.dss.spi.exception.DSSExternalResourceException;
import eu.europa.esig.dss.utils.Utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.util.concurrent.Callable;

/**
 * The call of native java DataLoader using the java.net.URL class.
 * This class returns an instance of {@code eu.europa.esig.dss.model.http.HTTPResponse} object.
 * Please set the variable {@code includeHttpContent} should you need an extended HTTP content data
 * (HTTP headers, SSL session parameters, etc.). If not set, only the response message body is to be returned.
 *
 */
public class NativeHTTPDataLoaderCall implements Callable<ResponseEnvelope> {

    /** The default error message */
    private static final String ERROR_MESSAGE = "An error occurred while reading from url '%s' : %s";

    /** The URL */
    private final String url;

    /** The content of the request, when processing a POST request */
    private final byte[] content;

    /** Defines the cache is used */
    private boolean useCaches;

    /** Max input size */
    private int maxInputSize;

    /** Timeout on opening a connection with remote resource */
    private int connectTimeout;

    /** Timeout on reading a response from remote resource */
    private int readTimeout;

    /** Defines whether the response message body is to be included in the final response object */
    private boolean includeResponseBody = true;

    /** Defines whether the extended HTTP content (headers, TLS/SSL certificates, etc.) should be included in the result */
    private boolean includeResponseDetails;

    /**
     * Constructor for a GET call instantiation
     *
     * @param url {@link String}
     */
    public NativeHTTPDataLoaderCall(String url) {
        this(url, null);
    }

    /**
     * Constructor for a POST call instantiation
     *
     * @param url {@link String}
     * @param content byte array
     */
    public NativeHTTPDataLoaderCall(String url, byte[] content) {
        this.url = url;
        this.content = content;
    }

    /**
     * Sets whether the caches shall be used.
     *
     * @param useCaches whether the caches shall be used
     */
    public void setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
    }

    /**
     * Sets the limit size of the HTTP response body.
     * If the HTTP response exceeds the given value, an exception will be thrown.
     * For 0 or negative value, no limit will be exposed.
     *
     * @param maxInputSize limit size of the HTTP response body
     */
    public void setMaxInputSize(int maxInputSize) {
        this.maxInputSize = maxInputSize;
    }

    /**
     * Sets a connection timeout
     *
     * @param connectTimeout timeout value in milliseconds
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Sets a read timeout
     *
     * @param readTimeout timeout value in milliseconds
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Sets whether the response message body is to be included in the final response object.
     * Default : TRUE (the final ResponseEnvelope contains the response body)
     *
     * @param includeResponseBody whether the response message body is to be included in the final response object
     */
    public void setIncludeResponseBody(boolean includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    /**
     * Sets whether additional HTTP response content is to be included in the final object (e.g. TLS/SSL certificates,
     * headers, etc.).
     * Default : FALSE (the final ResponseEnvelope does not contain the request metadata and/or context)
     *
     * @param includeResponseDetails whether additional HTTP response content is to be included in the final object
     */
    public void setIncludeResponseDetails(boolean includeResponseDetails) {
        this.includeResponseDetails = includeResponseDetails;
    }

    @Override
    public ResponseEnvelope call() {
        OutputStream os = null;
        InputStream is = null;

        try {
            final ResponseEnvelope result = new ResponseEnvelope();

            URLConnection connection = createConnection();
            connection.setUseCaches(useCaches);
            if (connectTimeout > 0) {
                connection.setConnectTimeout(connectTimeout);
            }
            if (readTimeout > 0) {
                connection.setReadTimeout(readTimeout);
            }
            if (content != null) {
                connection.setDoOutput(true);
                os = connection.getOutputStream();
                Utils.write(content, os);
            }

            is = connection.getInputStream();
            if (includeResponseBody) {
                if (maxInputSize > 0) {
                    is = new MaxSizeInputStream(is, maxInputSize, url);
                }
                result.setResponseBody(Utils.toByteArray(is));
            }

            if (includeResponseDetails) {
                result.setHeaders(connection.getHeaderFields());

                // If it's HTTPS, retrieve SSL session after connecting
                if (connection instanceof HttpsURLConnection) {
                    HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                    Certificate[] certificates = httpsConnection.getServerCertificates();
                    result.setTLSCertificates(certificates);
                }
            }

            return result;

        } catch (IOException e) {
            throw new DSSExternalResourceException(String.format(ERROR_MESSAGE, url, e.getMessage()), e);

        } finally {
            Utils.closeQuietly(os);
            Utils.closeQuietly(is);
        }
    }

    /**
     * Creates connection
     *
     * @return {@link URLConnection}
     * @throws IOException if IOException occurred
     */
    protected URLConnection createConnection() throws IOException {
        return URI.create(url).toURL().openConnection();
    }

}
