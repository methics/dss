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
package eu.europa.esig.dss.validation.executor.certificate;

import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.simplecertificatereport.jaxb.XmlSimpleCertificateReport;
import eu.europa.esig.dss.validation.executor.AbstractProcessExecutor;
import eu.europa.esig.dss.validation.reports.CertificateReports;

import java.util.Objects;

/**
 * Executes a certificate validation
 */
public class DefaultCertificateProcessExecutor extends AbstractProcessExecutor implements CertificateProcessExecutor {

	/** Id of a certificate to validate */
	protected String certificateId;

	/**
	 * Default constructor instantiating object with null certificate id
	 */
	public DefaultCertificateProcessExecutor() {
		// empty
	}

	@Override
	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}

	@Override
	public CertificateReports execute() {
		assertConfigurationValid();
		Objects.requireNonNull(certificateId, "The certificate id is missing");

		DiagnosticData diagnosticData = getDiagnosticData();

		DetailedReportForCertificateBuilder detailedReportBuilder = getDetailedReportBuilder(diagnosticData);
		XmlDetailedReport xmlDetailedReport = detailedReportBuilder.build();
		DetailedReport detailedReport = new DetailedReport(xmlDetailedReport);

		SimpleReportForCertificateBuilder simpleReportBuilder = getSimpleReportBuilder(diagnosticData, detailedReport);
		XmlSimpleCertificateReport simpleReport = simpleReportBuilder.build();

		return new CertificateReports(jaxbDiagnosticData, xmlDetailedReport, simpleReport);
	}

	/**
	 * Gets the Diagnostic Data
	 *
	 * @return {@link DiagnosticData}
	 */
	protected DiagnosticData getDiagnosticData() {
		return new DiagnosticData(jaxbDiagnosticData);
	}

	/**
	 * Gets the Detailed report builder
	 *
	 * @param diagnosticData {@link DiagnosticData}
	 * @return {@link DetailedReportForCertificateBuilder}
	 */
	protected DetailedReportForCertificateBuilder getDetailedReportBuilder(DiagnosticData diagnosticData) {
		return new DetailedReportForCertificateBuilder(getI18nProvider(), diagnosticData, policy, currentTime, certificateId);
	}

	/**
	 * Gets the Simple report builder
	 *
	 * @param diagnosticData {@link DiagnosticData}
	 * @param detailedReport {@link DetailedReport}
	 * @return {@link DetailedReportForCertificateBuilder}
	 */
	protected SimpleReportForCertificateBuilder getSimpleReportBuilder(DiagnosticData diagnosticData, DetailedReport detailedReport) {
		return new SimpleReportForCertificateBuilder(diagnosticData, detailedReport, policy, currentTime, certificateId);
	}

}
