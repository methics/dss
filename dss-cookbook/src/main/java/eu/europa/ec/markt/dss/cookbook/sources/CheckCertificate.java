/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.ec.markt.dss.cookbook.sources;

import java.util.Set;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.dss.cookbook.example.Cookbook;
import eu.europa.ec.markt.dss.cookbook.mock.MockCRLSource;
import eu.europa.ec.markt.dss.cookbook.mock.MockEmptyTSLCertificateSource;
import eu.europa.ec.markt.dss.cookbook.mock.MockOCSPSource;
import eu.europa.ec.markt.dss.validation102853.CertificatePool;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.dss.validation102853.CertificateVerifier;
import eu.europa.ec.markt.dss.validation102853.CommonCertificateSource;
import eu.europa.ec.markt.dss.validation102853.CommonCertificateVerifier;
import eu.europa.ec.markt.dss.validation102853.CommonTrustedCertificateSource;
import eu.europa.ec.markt.dss.validation102853.RevocationToken;
import eu.europa.ec.markt.dss.validation102853.SignatureValidationContext;
import eu.europa.ec.markt.dss.validation102853.crl.OnlineCRLSource;
import eu.europa.ec.markt.dss.validation102853.ocsp.OnlineOCSPSource;

/**
 * How to check a certificate
 */
public class CheckCertificate extends Cookbook {

	public static void main(String[] args) {

		checkMockCertificate();

		checkRealCertificate();
	}

	private static void checkRealCertificate() {

		CertificateToken toValidateX509Certificate = DSSUtils.loadCertificate("/toValidate.crt");

		CertificateToken issuerCert = DSSUtils.loadCertificate("/trusted.crt");
		CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();
		trustedCertificateSource.addCertificate(issuerCert);

		CommonCertificateSource adjunctCertificateSource = new CommonCertificateSource();
		CertificateToken intermediateCert = DSSUtils.loadCertificate("/intermediate.cer");
		adjunctCertificateSource.addCertificate(intermediateCert);
		CertificateToken toValidateCertificateToken = adjunctCertificateSource.addCertificate(toValidateX509Certificate);

		CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
		certificateVerifier.setTrustedCertSource(trustedCertificateSource);
		certificateVerifier.setAdjunctCertSource(adjunctCertificateSource);
		OnlineCRLSource crlSource = new OnlineCRLSource();
		certificateVerifier.setCrlSource(crlSource);
		certificateVerifier.setOcspSource(new OnlineOCSPSource());

		final CertificatePool validationPool = certificateVerifier.createValidationPool();
		SignatureValidationContext validationContext = new SignatureValidationContext(validationPool);
		validationContext.addCertificateTokenForVerification(toValidateCertificateToken);
		validationContext.validate();

		System.out.println(toValidateCertificateToken);

		toValidateCertificateToken.isRevoked();
		final RevocationToken revocationToken = toValidateCertificateToken.getRevocationToken();
		/// ...

		Set<CertificateToken> certTokens = validationContext.getProcessedCertificates();
		for (CertificateToken certToken : certTokens) {

			System.out.println(certToken);
		}
	}

	public static void checkMockCertificate() {

		final CertificateToken issuerCert = DSSUtils.loadCertificate("/belgiumrs2.crt");
		MockEmptyTSLCertificateSource trustedCertificateSource = new MockEmptyTSLCertificateSource();
		trustedCertificateSource.addCertificate(issuerCert);

		CommonCertificateSource adjunctCertificateSource = new CommonCertificateSource();
		final CertificateToken endUserCert = DSSUtils.loadCertificate("/citizen_ca.cer");
		final CertificateToken endUserCertToken = adjunctCertificateSource.addCertificate(endUserCert);

		final CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
		certificateVerifier.setTrustedCertSource(trustedCertificateSource);
		certificateVerifier.setAdjunctCertSource(adjunctCertificateSource);
		certificateVerifier.setCrlSource(new MockCRLSource("/revocation/belgium2.crl"));
		certificateVerifier.setOcspSource(new MockOCSPSource("/ocsp/1302521088270.der"));

		final CertificatePool validationPool = certificateVerifier.createValidationPool();
		final SignatureValidationContext validationContext = new SignatureValidationContext(validationPool);
		validationContext.addCertificateTokenForVerification(endUserCertToken);
		validationContext.validate();

		System.out.println(endUserCertToken);

		endUserCertToken.isRevoked();

		final Set<CertificateToken> certTokens = validationContext.getProcessedCertificates();
		for (final CertificateToken certToken : certTokens) {

			System.out.println(certToken);
		}
	}
}
