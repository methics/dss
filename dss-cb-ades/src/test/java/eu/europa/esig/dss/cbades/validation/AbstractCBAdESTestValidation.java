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
package eu.europa.esig.dss.cbades.validation;

import eu.europa.esig.dss.diagnostic.CertificateRefWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.enumerations.CertificateRefOrigin;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.test.validation.AbstractDocumentTestValidation;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.validationreport.jaxb.SADataObjectFormatType;
import eu.europa.esig.validationreport.jaxb.SignatureIdentifierType;
import eu.europa.esig.validationreport.jaxb.SignatureValidationReportType;
import eu.europa.esig.validationreport.jaxb.ValidationReportType;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractCBAdESTestValidation extends AbstractDocumentTestValidation {

    @Override
    protected SignedDocumentValidator getValidator(DSSDocument signedDocument) {
        COSEDocumentValidator documentValidator = (COSEDocumentValidator) super.getValidator(signedDocument);
        documentValidator.setExternallySuppliedData(getExternallySuppliedData());
        return documentValidator;
    }

    protected DSSDocument getExternallySuppliedData() {
        return null;
    }

    @Override
    protected void checkContentType(DiagnosticData diagnosticData) {
        super.checkContentType(diagnosticData);

        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            assertNull(signatureWrapper.getContentType());
        }
    }

    @Override
    protected void checkSignatureIdentifier(DiagnosticData diagnosticData) {
        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            assertNotNull(signatureWrapper.getSignatureValue());
        }
    }

    @Override
    protected void checkReportsSignatureIdentifier(Reports reports) {
        DiagnosticData diagnosticData = reports.getDiagnosticData();
        ValidationReportType etsiValidationReport = reports.getEtsiValidationReportJaxb();
        for (SignatureValidationReportType signatureValidationReport : etsiValidationReport.getSignatureValidationReport()) {
            SignatureWrapper signature = diagnosticData.getSignatureById(signatureValidationReport.getSignatureIdentifier().getId());

            SignatureIdentifierType signatureIdentifier = signatureValidationReport.getSignatureIdentifier();
            assertNotNull(signatureIdentifier);

            assertNotNull(signatureIdentifier.getSignatureValue());
            assertArrayEquals(signature.getSignatureValue(), signatureIdentifier.getSignatureValue().getValue());
        }
    }

    @Override
    protected void checkMessageDigestAlgorithm(DiagnosticData diagnosticData) {
        super.checkMessageDigestAlgorithm(diagnosticData);

        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            for (XmlDigestMatcher digestMatcher : signatureWrapper.getDigestMatchers()) {
                if (DigestMatcherType.COSE_SIG_STRUCTURE.equals(digestMatcher.getType()) ||
                        DigestMatcherType.SIG_D_ENTRY.equals(digestMatcher.getType())) {
                    assertNotNull(digestMatcher.getDigestMethod());
                    assertNotNull(digestMatcher.getDigestValue());
                } else if (DigestMatcherType.COUNTER_SIGNED_SIGNATURE_VALUE.equals(digestMatcher.getType())) {
                    assertNull(digestMatcher.getDigestMethod());
                    assertNull(digestMatcher.getDigestValue());
                } else {
                    fail(String.format("Unexpected DigestMatcherType reached : %s", digestMatcher.getType()));
                }
            }
        }
    }

    @Override
    protected void checkSigningCertificateValue(DiagnosticData diagnosticData) {
        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            assertTrue(signatureWrapper.isSigningCertificateIdentified());
            assertTrue(signatureWrapper.isSigningCertificateReferencePresent());

            CertificateRefWrapper signingCertificateReference = signatureWrapper.getSigningCertificateReference();
            assertNotNull(signingCertificateReference);
            assertTrue(signingCertificateReference.isDigestValuePresent());
            assertTrue(signingCertificateReference.isDigestValueMatch());
            if (signingCertificateReference.isIssuerSerialPresent()) {
                assertTrue(signingCertificateReference.isIssuerSerialMatch());
            }

            CertificateWrapper signingCertificate = signatureWrapper.getSigningCertificate();
            assertNotNull(signingCertificate);
            String signingCertificateId = signingCertificate.getId();
            String certificateDN = diagnosticData.getCertificateDN(signingCertificateId);
            String certificateSerialNumber = diagnosticData.getCertificateSerialNumber(signingCertificateId);
            assertEquals(signingCertificate.getCertificateDN(), certificateDN);
            assertEquals(signingCertificate.getSerialNumber(), certificateSerialNumber);

            assertTrue(Utils.isCollectionEmpty(signatureWrapper.foundCertificates()
                    .getOrphanCertificatesByRefOrigin(CertificateRefOrigin.SIGNING_CERTIFICATE)));
        }
    }

    @Override
    protected void validateETSIDataObjectFormatType(SADataObjectFormatType dataObjectFormat) {
        super.validateETSIDataObjectFormatType(dataObjectFormat);

        assertNull(dataObjectFormat.getContentType());
        assertNotNull(dataObjectFormat.getMimeType());
    }

}
