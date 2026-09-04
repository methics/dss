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
package eu.europa.esig.dss.spi.x509.aia;

import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.spi.x509.TokenIssuerSelector;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The certificate source requesting issuer certificates by AIA
 *
 */
public class AIACertificateSource extends CommonCertificateSource {

	private static final long serialVersionUID = -2604947158902474169L;

	private static final Logger LOG = LoggerFactory.getLogger(AIACertificateSource.class);

	/** The certificate token to get issuer for */
	private final CertificateToken certificate;

	/**
	 * Constructor to create the AIA certificate source for a {@code certificate}
	 *
	 * @param certificate {@link CertificateToken}
	 */
	protected AIACertificateSource(final CertificateToken certificate) {
		Objects.requireNonNull(certificate, "The certificate cannot be null");
		this.certificate = certificate;
	}

	/**
	 * Retrieves an AIA.caIssuers for the given {@code certificate} using the {@code aiaSource}.
	 * NOTE: This method performs AIA URI request on instantiation.
	 *
	 * @param certificate {@link CertificateToken} to get AIA.caIssuers for
	 * @param aiaSource {@link AIASource} to use
	 * @return {@link AIACertificateSource}
	 */
	public static AIACertificateSource forCertificateToken(final CertificateToken certificate, final AIASource aiaSource) {
		LOG.info("Retrieving {} certificate's issuer using AIA.", certificate.getAbbreviation());

		final AIACertificateSource aiaCertificateSource = new AIACertificateSource(certificate);
		try {
			Set<CertificateToken> extractedCertificates = new LinkedHashSet<>(aiaSource.getCertificatesByAIA(certificate));
			if (Utils.isCollectionNotEmpty(extractedCertificates)) {
				CertificateToken currentCertificate = certificate;
				while (currentCertificate != null) {
					CertificateToken issuer = getIssuer(currentCertificate, extractedCertificates);
					if (aiaCertificateSource.getCertificates().contains(issuer)) {
						// break for processed certificates
						break;

					} else if (issuer != null) {
						// add issuer for processing
						aiaCertificateSource.addCertificate(issuer);

					}
					currentCertificate = issuer;
				}

				// if no certificates have been extracted -> add all
				if (Utils.isCollectionEmpty(aiaCertificateSource.getCertificates())) {
					for (CertificateToken certificateToken : extractedCertificates) {
						aiaCertificateSource.addCertificate(certificateToken);
					}
				}

			} else {
				LOG.warn("No AIA certificates have been retrieved for a certificate with Id '{}'", certificate.getDSSIdAsString());
			}

		} catch (Exception e) {
			LOG.warn("An error occurred on attempt to retrieve certificate's CA issuers : {}", e.getMessage(), e);
		}
		return aiaCertificateSource;
	}

	/**
	 * Gets issuer certificate for the {@code certificate} from the given collection of {@code candidates}.
	 * This method returns a NULL value if no suitable issuer was found.
	 *
	 * @param certificate {@link CertificateToken} to get issuer for
	 * @param candidates a collection of {@link CertificateToken}s
	 * @return {@link CertificateToken} issuer of the certificate if found, NULL otherwise
	 */
	protected static CertificateToken getIssuer(final CertificateToken certificate, Collection<CertificateToken> candidates) {
		CertificateToken issuer = new TokenIssuerSelector(certificate, candidates).getIssuer();
		return issuer != null && certificate.isSignedBy(issuer) ? issuer : null;
	}

	/**
	 * Get the issuer's certificate from Authority Information Access through
	 * id-ad-caIssuers extension.
	 *
	 * @return {@code CertificateToken} representing the issuer certificate or null.
	 */
	public CertificateToken getIssuerFromAIA() {
		Collection<CertificateToken> candidates = getCertificates();
		if (Utils.isCollectionNotEmpty(candidates)) {
			// The potential issuers might support 3 known scenarios:
			// - issuer certificate with single entry
			// - issuer certificate is a collection of bridge certificates (all having the
			// same public key)
			// - full certification path (up to the root of the chain)
			// In case the issuer is a collection of bridge certificates, only one of the
			// bridge certificates needs to be verified
			CertificateToken issuer = getIssuer(certificate, candidates);
			if (issuer == null) {
				LOG.warn("The retrieved certificate(s) using AIA do not sign the certificate with Id : {}.",
						certificate.getDSSIdAsString());
			}
			return issuer;
		}
		return null;
	}

	@Override
	public CertificateSourceType getCertificateSourceType() {
		return CertificateSourceType.AIA;
	}

}
