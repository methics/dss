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
package eu.europa.esig.dss.validation.job.source;

import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.job.cache.CacheKey;

import java.util.Objects;

/**
 * Represent a Trusted List source
 */
public class DocumentSource {

	/**
	 * URL
	 */
	private String url;

	/**
	 * Signing certificates for the current document
	 */
	private CertificateSource certificateSource;
	
	/**
	 * The cached CacheKey value (the key is computed from url parameter)
	 */
	private CacheKey cacheKey;

	/**
	 * Default constructor instantiating object with null values
	 */
	public DocumentSource() {
		// empty
	}

	/**
	 * Gets the document URL
	 *
	 * @return {@link String}
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the document access URL
	 *
	 * @param url {@link String}
	 */
	public void setUrl(String url) {
		Objects.requireNonNull(url, "URL cannot be null.");
		this.url = url;
	}

	/**
	 * Gets the certificate source to be used for document validation
	 *
	 * @return {@link CertificateSource}
	 */
	public CertificateSource getCertificateSource() {
		return certificateSource;
	}

	/**
	 * Sets the certificate source to be used for document validation
	 *
	 * @param certificateSource {@link CertificateSource}
	 */
	public void setCertificateSource(CertificateSource certificateSource) {
		Objects.requireNonNull(certificateSource);
		this.certificateSource = certificateSource;
	}

	/**
	 * Gets the document cache key
	 *
	 * @return {@link CacheKey}
	 */
	public CacheKey getCacheKey() {
		if (cacheKey == null) {
			cacheKey = new CacheKey(url);
		}
		return cacheKey;
	}

}
