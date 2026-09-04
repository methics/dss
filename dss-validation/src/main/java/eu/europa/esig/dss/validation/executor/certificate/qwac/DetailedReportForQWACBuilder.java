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
package eu.europa.esig.dss.validation.executor.certificate.qwac;

import eu.europa.esig.dss.detailedreport.jaxb.XmlBasicBuildingBlocks;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCertificate;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConclusion;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraintsConclusionWithProofOfExistence;
import eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlQWACProcess;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSignature;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationProcessBasicSignature;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.QWACProfile;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.executor.certificate.DetailedReportForCertificateBuilder;
import eu.europa.esig.dss.validation.process.qualification.certificate.CertificateQualificationBlock;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.CertificateQualificationForQWACBlock;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.QWACForTLSCertificateValidationBlock;
import eu.europa.esig.dss.validation.process.qualification.signature.SignatureQualificationBlock;
import eu.europa.esig.dss.validation.process.qualification.signature.qwac.TLSBindingSignatureQualificationBlock;
import eu.europa.esig.dss.validation.process.vpfbs.BasicSignatureValidationProcess;
import eu.europa.esig.dss.validation.reports.DSSReportException;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Builds a Detailed Report for a QWAC certificate validation
 *
 */
public class DetailedReportForQWACBuilder extends DetailedReportForCertificateBuilder {

    /**
     * Default constructor
     *
     * @param i18nProvider   {@link I18nProvider}
     * @param diagnosticData {@link DiagnosticData}
     * @param policy         {@link ValidationPolicy}
     * @param currentTime    {@link Date} validation time
     * @param certificateId  {@link String} id of a certificate to be validated
     */
    public DetailedReportForQWACBuilder(I18nProvider i18nProvider, DiagnosticData diagnosticData,
                                        ValidationPolicy policy, Date currentTime, String certificateId) {
        super(i18nProvider, diagnosticData, policy, currentTime, certificateId);
    }

    @Override
    protected Map<String, XmlBasicBuildingBlocks> executeAllBasicBuildingBlocks() {
        Map<String, XmlBasicBuildingBlocks> bbbs = super.executeAllBasicBuildingBlocks();
        process(diagnosticData.getAllSignatures(), Context.SIGNATURE, bbbs);
        return bbbs;
    }

    @Override
    protected void executeValidation(XmlDetailedReport detailedReport, Map<String, XmlBasicBuildingBlocks> bbbs) {
        XmlSignature xmlSignature = buildXmlTLSCertificateBindingSignature(detailedReport, bbbs);
        buildXmlCertificate(detailedReport, bbbs, xmlSignature);
    }

    /**
     * Validates and builds a report information for validation of a TLS Certificate Binding signature, when present
     *
     * @param detailedReport {@link XmlDetailedReport}
     * @param bbbs a map of {@link XmlBasicBuildingBlocks}
     * @return {@link XmlSignature}
     */
    protected XmlSignature buildXmlTLSCertificateBindingSignature(XmlDetailedReport detailedReport,
                                                          Map<String, XmlBasicBuildingBlocks> bbbs) {
        SignatureWrapper bindingSignature = diagnosticData.getTLSCertificateBindingSignature();
        if (bindingSignature == null) {
            return null;
        }

        final XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setId(bindingSignature.getId());

        XmlConstraintsConclusionWithProofOfExistence validation = executeBasicValidation(xmlSignature, bindingSignature, bbbs);

        if (policy.isEIDASConstraintPresent()) {

            // Signature qualification
            SignatureQualificationBlock qualificationBlock = new TLSBindingSignatureQualificationBlock(
                    i18nProvider, bbbs, validation, bindingSignature, detailedReport.getTLAnalysis(), diagnosticData.getWebsiteUrl());
            xmlSignature.setValidationSignatureQualification(qualificationBlock.execute());

        }

        XmlConclusion conclusion = validation.getConclusion();
        conclusion.setIndication(getSignatureFinalIndication(conclusion.getIndication()));
        xmlSignature.setConclusion(conclusion);

        detailedReport.getSignatureOrTimestampOrEvidenceRecord().add(xmlSignature);

        return xmlSignature;
    }

    private XmlValidationProcessBasicSignature executeBasicValidation(XmlSignature signatureAnalysis, SignatureWrapper signature,
                                                                      Map<String, XmlBasicBuildingBlocks> bbbs) {
        BasicSignatureValidationProcess vpfbs = new BasicSignatureValidationProcess(
                i18nProvider, diagnosticData, signature, Collections.emptyList(), bbbs);
        XmlValidationProcessBasicSignature bs = vpfbs.execute();
        signatureAnalysis.setValidationProcessBasicSignature(bs);
        return bs;
    }

    /**
     * Builds XmlCertificate
     *
     * @param detailedReport {@link XmlDetailedReport}
     * @param bbbs a map of {@link XmlBasicBuildingBlocks}
     * @param bindingSignature {@link XmlSignature}
     * @return {@link XmlCertificate}
     */
    protected XmlCertificate buildXmlCertificate(XmlDetailedReport detailedReport, Map<String, XmlBasicBuildingBlocks> bbbs,
                                                 XmlSignature bindingSignature) {
        XmlCertificate xmlCertificate = super.buildXmlCertificate(detailedReport, bbbs);

        QWACForTLSCertificateValidationBlock qwacForTLSCertificateValidationBlock = new QWACForTLSCertificateValidationBlock(
                i18nProvider, diagnosticData, getCertificate(), bbbs, xmlCertificate.getCertificateQualificationProcess(),
                getQWACProfile(bindingSignature), diagnosticData.getWebsiteUrl());
        XmlQWACProcess xmlQWACProcess = qwacForTLSCertificateValidationBlock.execute();
        xmlCertificate.setQWACProcess(xmlQWACProcess);

        return xmlCertificate;
    }

    @Override
    protected CertificateQualificationBlock getCertificateQualificationBlock(XmlDetailedReport detailedReport, XmlBasicBuildingBlocks basicBuildingBlocks) {
        return new CertificateQualificationForQWACBlock(i18nProvider, basicBuildingBlocks.getConclusion(), currentTime,
                getCertificate(), detailedReport.getTLAnalysis());
    }

    private QWACProfile getQWACProfile(XmlSignature bindingSignature) {
        if (bindingSignature != null && bindingSignature.getValidationSignatureQualification() != null) {
            XmlQWACProcess qwacProcess = bindingSignature.getValidationSignatureQualification().getQWACProcess();
            if (qwacProcess != null) {
                return qwacProcess.getQWACType();
            }
        }
        return QWACProfile.NOT_QWAC;
    }

    private Indication getSignatureFinalIndication(Indication highestIndication) {
        switch (highestIndication) {
            case PASSED:
                return Indication.TOTAL_PASSED;
            case INDETERMINATE:
                return Indication.INDETERMINATE;
            case FAILED:
                return Indication.TOTAL_FAILED;
            default:
                throw new DSSReportException(String.format("The Indication '%s' is not supported!", highestIndication));
        }
    }

}
