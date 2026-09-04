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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Enumeration defines a list of algorithm described in ETSI TS 119 182-1 and TS 119 152-1
 * for incorporation of 'sigD' dictionary
 * (see 5.2.8 The sigD header parameter and 5.2.9 The sigD header parameter, respectively)
 *
 */
public enum SigDMechanism {
	
	/**
	 * 5.2.8.2 Mechanism HttpHeaders
	 */
	HTTP_HEADERS("http://uri.etsi.org/19182/HttpHeaders", null),

	/**
	 * 5.2.8.3.2 Mechanism ObjectIdByURI
	 */
	OBJECT_ID_BY_URI("http://uri.etsi.org/19182/ObjectIdByURI", "http://uri.etsi.org/19152/ObjectIdByURI"),

	/**
	 * 5.2.8.3.3 Mechanism ObjectIdByURIHash
	 * <p>
	 * NOTE: the default signature creation mechanism used by DSS
	 */
	OBJECT_ID_BY_URI_HASH("http://uri.etsi.org/19182/ObjectIdByURIHash", "http://uri.etsi.org/19152/ObjectIdByURIHash"),
	
	/**
	 * Creates a simple DETACHED signature with omitted payload (without SigD element)
	 */
	NO_SIG_D("", "");

	private static final Logger LOG = LoggerFactory.getLogger(SigDMechanism.class);

	/** JAdES ETSI TS 119 182-1 URI */
	private final String jadesUri;

	/** CB-AdES ETSI TS 119 152-1 URI */
	private final String cbadesUri;

	/**
	 * Default constructor
	 *
	 * @param jadesUri {@link String} JAdES ETSI TS 119 182-1 URI
	 * @param cbadesUri {@link String} CB-AdES ETSI TS 119 152-1 URI
	 */
	SigDMechanism(final String jadesUri, final String cbadesUri) {
		this.jadesUri = jadesUri;
		this.cbadesUri = cbadesUri;
	}

	/**
	 * Returns a JAdES ETSI TS 119 182-1 URI
	 *
	 * @return {@link String}
	 */
	public String getJAdESUri() {
		return jadesUri;
	}

	/**
	 * Returns a CB-AdES ETSI TS 119 152-1 URI
	 *
	 * @return {@link String}
	 */
	public String getCBAdESUri() {
		return cbadesUri;
	}

	/**
	 * Returns a SigDMechanism for the given JAdES ETSI TS 119 182-1 URI
	 *
	 * @param uri {@link String} URI representing a SigDMechanism
	 * @return {@link SigDMechanism}
	 */
	public static SigDMechanism forJAdESUri(final String uri) {
		for (SigDMechanism sigDMechanism : values()) {
			if (sigDMechanism.getJAdESUri().equals(uri)) {
				return sigDMechanism;
			}
		}
		return null;
	}

	/**
	 * Returns a SigDMechanism for the given CB-AdES ETSI TS 119 152-1 URI
	 *
	 * @param uri {@link String} URI representing a SigDMechanism
	 * @return {@link SigDMechanism}
	 */
	public static SigDMechanism forCBAdESUri(final String uri) {
		for (SigDMechanism sigDMechanism : values()) {
			if (sigDMechanism.getCBAdESUri() != null && sigDMechanism.getCBAdESUri().equals(uri)) {
				return sigDMechanism;
			}
		}
		return null;
	}

}
