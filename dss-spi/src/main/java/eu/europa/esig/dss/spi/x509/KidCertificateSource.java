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
package eu.europa.esig.dss.spi.x509;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The certificate source containing a map of certificates by KIDs.
 * The class is used for JAdES and CB-AdES processing.
 *
 */
public class KidCertificateSource extends CommonCertificateSource {

	private static final long serialVersionUID = 8202022366337914356L;

	private static final Logger LOG = LoggerFactory.getLogger(KidCertificateSource.class);

	/** Map of kids and related certificate tokens */
	private Map<String, CertificateToken> mapByKid = new HashMap<>();

	/**
	 * Default constructor instantiating object with empty map of 'kid' identifiers and certificate tokens relation
	 */
	public KidCertificateSource() {
		// empty
	}

	@Override
	public CertificateToken addCertificate(CertificateToken certificateToAdd) {
		Objects.requireNonNull(certificateToAdd, "The certificate must be filled");
		LOG.debug("kid is not provided (generate kid following the JAdES specification)");
		return addCertificate(generateKidBase64String(certificateToAdd), certificateToAdd);
	}

	/**
	 * Generates the 'kid' value as in IETF RFC 5035
	 *
	 * @param signingCertificate {@link CertificateToken} representing the singing
	 *                           certificate
	 * @return {@link String} 'kid' header value
	 */
	protected String generateKidBase64String(CertificateToken signingCertificate) {
		return Utils.toBase64(DSSUtils.generateKid(signingCertificate));
	}

	/**
	 * Adds a certificate for a given String 'kid' (JWS)
	 * 
	 * @param kid         {@link String} representing the key identifier of the corresponding certificate
	 * @param certificate the related certificate token
	 * @return the certificate
	 */
	public CertificateToken addCertificate(String kid, CertificateToken certificate) {
		Objects.requireNonNull(kid, "kid cannot be null!");
		CertificateToken addedCertificate = super.addCertificate(certificate);
		if (mapByKid.containsKey(kid)) {
			LOG.warn("kid value '{}' is already known, the certificate will be replaced", kid);
		}
		mapByKid.put(kid, addedCertificate);
		return addedCertificate;
	}

	/**
	 * Adds a certificate for a given byte array 'kid' (COSE).
	 * NOTE: when used, the value of the byte array kid is to be base64-encoded for a preservation within a map
	 *
	 * @param kid         byte array representing the key identifier of the corresponding certificate
	 * @param certificate the related certificate token
	 * @return the certificate
	 */
	public CertificateToken addCertificate(byte[] kid, CertificateToken certificate) {
		Objects.requireNonNull(kid, "kid cannot be null!");
		return addCertificate(Utils.toBase64(kid), certificate);
	}

	/**
	 * Gets a {@code CertificateToken} by the given base64-encoded KID value
	 *
	 * @param kid {@link String} of base64-encoded value of 'kid' to get a certificate token with
	 * @return {@link CertificateToken}
	 */
	public CertificateToken getCertificateByKid(String kid) {
		return mapByKid.get(kid);
	}

	/**
	 * Gets a {@code CertificateToken} by the given binary KID value
	 *
	 * @param kid byte array representing a 'kid' header value to get a certificate token with
	 * @return {@link CertificateToken}
	 */
	public CertificateToken getCertificateByKid(byte[] kid) {
		return mapByKid.get(Utils.toBase64(kid));
	}

	@Override
	public Set<CertificateToken> findTokensFromCertRef(CertificateRef certificateRef) {
		final Set<CertificateToken> certificates = super.findTokensFromCertRef(certificateRef);
		if (Utils.isStringNotEmpty(certificateRef.getKid())) {
			CertificateToken kidCertificate = mapByKid.get(certificateRef.getKid());
			if (kidCertificate != null) {
				certificates.add(kidCertificate);
			}
		}
		return certificates;
	}

	@Override
	protected void reset() {
		super.reset();
		mapByKid = new HashMap<>();
	}

}
