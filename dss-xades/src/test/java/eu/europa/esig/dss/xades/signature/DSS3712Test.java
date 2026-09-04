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
package eu.europa.esig.dss.xades.signature;

import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pki.x509.aia.PKIAIASource;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESTimestampParameters;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DSS3712Test extends AbstractXAdESTestSignature {

    private DocumentSignatureService<XAdESSignatureParameters, XAdESTimestampParameters> service;
    private DSSDocument documentToSign;

    private Date signingDate;

    private int aiaSourceCalls = 0;

    @BeforeEach
    void init() throws Exception {
        service = new XAdESService(getOfflineCertificateVerifier());
        documentToSign = new FileDocument(new File("src/test/resources/sample.xml"));
        signingDate = new Date();
    }

    @Override
    protected XAdESSignatureParameters getSignatureParameters() {
        // Stateless mode
        XAdESSignatureParameters signatureParameters = new XAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(signingDate);
        signatureParameters.setSigningCertificate(getSigningCert());
        // do not include certificate chain
        signatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
        signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        return signatureParameters;
    }

    @Override
    protected SignedDocumentValidator getValidator(DSSDocument signedDocument) {
        SignedDocumentValidator documentValidator = super.getValidator(signedDocument);
        documentValidator.setCertificateVerifier(getCompleteCertificateVerifier());
        return documentValidator;
    }

    @Override
    protected CertificateSource getTrustedCertificateSource() {
        CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();
        trustedCertificateSource.addCertificate(getCertificate("Test-National-RootCA-from-ZZ"));
        return trustedCertificateSource;
    }

    @Override
    protected CertificateVerifier getCompleteCertificateVerifier() {
        CertificateVerifier certificateVerifier = super.getCompleteCertificateVerifier();
        certificateVerifier.setAIASource(pkiAIASource());
        certificateVerifier.setOcspSource(null); // skip OCSP, to avoid CA certificates from OCSP responses
        return certificateVerifier;
    }

    @Override
    protected PKIAIASource pkiAIASource() {
        return new PKIAIASource(getCertEntityRepository()) {

            @Override
            public Set<CertificateToken> getCertificatesByAIA(CertificateToken certificateToken) {
                ++aiaSourceCalls;
                return super.getCertificatesByAIA(certificateToken);
            }

            @Override
            protected boolean canGenerate(CertificateToken certificateToken) {
                if (getSigningCert().equals(certificateToken)) {
                    return super.canGenerate(certificateToken);
                }
                // no AIA for other certificates
                return false;
            }
        };
    }

    @Override
    protected void checkCertificateChain(DiagnosticData diagnosticData) {
        super.checkCertificateChain(diagnosticData);

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        assertNotNull(signature.getSigningCertificate());
        assertEquals(4, signature.getCertificateChain().size());

        int signatureSourceCounter = 0;
        int aiaSourceCounter = 0;
        for (CertificateWrapper certificateToken : signature.getCertificateChain()) {
            if (certificateToken.getSources().contains(CertificateSourceType.SIGNATURE)) {
                ++signatureSourceCounter;
            } else if (certificateToken.getSources().contains(CertificateSourceType.AIA)) {
                ++aiaSourceCounter;
            }
        }
        assertEquals(1, signatureSourceCounter);
        assertEquals(3, aiaSourceCounter);

        assertEquals(1, aiaSourceCalls);
    }

    @Override
    protected void verifySimpleReport(SimpleReport simpleReport) {
        super.verifySimpleReport(simpleReport);

        assertEquals(Indication.TOTAL_PASSED, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
    }

    @Override
    protected DocumentSignatureService<XAdESSignatureParameters, XAdESTimestampParameters> getService() {
        return service;
    }

    @Override
    protected DSSDocument getDocumentToSign() {
        return documentToSign;
    }

    @Override
    protected String getSigningAlias() {
        return "John Doe";
    }

}
