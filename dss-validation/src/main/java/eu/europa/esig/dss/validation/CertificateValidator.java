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
package eu.europa.esig.dss.validation;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.ValidationContext;
import eu.europa.esig.dss.validation.executor.certificate.CertificateProcessExecutor;
import eu.europa.esig.dss.validation.executor.certificate.DefaultCertificateProcessExecutor;
import eu.europa.esig.dss.validation.reports.CertificateReports;

import java.util.Objects;

/**
 * Validates a CertificateToken
 */
public class CertificateValidator extends AbstractCertificateValidator<CertificateReports, CertificateProcessExecutor> {

	/** The path for default certificate validation policy */
	private static final String CERTIFICATE_VALIDATION_POLICY_LOCATION = "/policy/certificate-constraint.xml";

	/**
	 * The certificateToken to be validated
	 */
	private final CertificateToken token;

	/**
	 * The default constructor
	 *
	 * @param token {@link CertificateToken}
	 */
	private CertificateValidator(CertificateToken token) {
		this.token = token;
	}

	/**
	 * Creates a CertificateValidator from a certificateToken
	 *
	 * @param token {@link CertificateToken}
	 * @return {@link CertificateValidator}
	 */
	public static CertificateValidator fromCertificate(final CertificateToken token) {
		Objects.requireNonNull(token, "The certificate is missing");
		return new CertificateValidator(token);
	}

	@Override
	protected String getDefaultValidationPolicyPath() {
		return CERTIFICATE_VALIDATION_POLICY_LOCATION;
	}

	@Override
	protected ValidationContext prepareValidationContext(CertificateVerifier certificateVerifier) {
		ValidationContext svc = super.prepareValidationContext(certificateVerifier);
		svc.addCertificateTokenForVerification(token);
		return svc;
	}

	/**
	 * Gets the {@link CertificateProcessExecutor}
	 *
	 * @return {@link CertificateProcessExecutor}
	 */
	protected CertificateProcessExecutor provideProcessExecutorInstance() {
		if (processExecutor == null) {
			processExecutor = getDefaultProcessExecutor();
		}
		processExecutor.setCertificateId(identifierProvider.getIdAsString(token));
		return processExecutor;
	}

	@Override
	public CertificateProcessExecutor getDefaultProcessExecutor() {
		return new DefaultCertificateProcessExecutor();
	}

	@Override
	protected void assertConfigurationValid() {
		super.assertConfigurationValid();
		Objects.requireNonNull(token, "Certificate token is not provided to the validator");
	}

}
