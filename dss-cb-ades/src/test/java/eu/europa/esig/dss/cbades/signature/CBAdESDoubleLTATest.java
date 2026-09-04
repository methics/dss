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
package eu.europa.esig.dss.cbades.signature;

import eu.europa.esig.dss.cbades.COSEHeaderParameter;
import eu.europa.esig.dss.cbades.cbor.CBORArray;
import eu.europa.esig.dss.cbades.cbor.CBORByteString;
import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;
import eu.europa.esig.dss.cbades.validation.AbstractCBAdESTestValidation;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.RelatedCertificateWrapper;
import eu.europa.esig.dss.diagnostic.RelatedRevocationWrapper;
import eu.europa.esig.dss.diagnostic.RevocationWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.COSEStructureType;
import eu.europa.esig.dss.enumerations.CertificateOrigin;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.enumerations.TokenExtractionStrategy;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CBAdESDoubleLTATest extends AbstractCBAdESTestValidation {

    @Test
    void test() throws IOException {
        DSSDocument documentToSign = new InMemoryDocument("Hello world!".getBytes());

        CBAdESSignatureParameters signatureParameters = new CBAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(new Date());
        signatureParameters.setSigningCertificate(getSigningCert());
        signatureParameters.setCertificateChain(getCertificateChain());
        signatureParameters.setSignatureLevel(SignatureLevel.CB_AdES_BASELINE_LT);
        signatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
        signatureParameters.setCoseStructureType(COSEStructureType.COSE_SIGN1);

        CBAdESService service = new CBAdESService(getCompleteCertificateVerifier());
        service.setTspSource(getGoodTsa());

        ToBeSigned dataToSign = service.getDataToSign(documentToSign, signatureParameters);
        SignatureValue signatureValue = getToken().sign(dataToSign, signatureParameters.getDigestAlgorithm(), getPrivateKeyEntry());
        DSSDocument signedDocument = service.signDocument(documentToSign, signatureParameters, signatureValue);

        // signedDocument.save("target/signed.cose");

        checkOnSigned(signedDocument, 0);

        service.setTspSource(getGoodTsaCrossCertification());

        CBAdESSignatureParameters extendParameters = new CBAdESSignatureParameters();
        extendParameters.setSignatureLevel(SignatureLevel.CB_AdES_BASELINE_LTA);
        extendParameters.setCoseStructureType(COSEStructureType.COSE_SIGN1);
        DSSDocument extendedDocument = service.extendDocument(signedDocument, extendParameters);

        checkOnSigned(extendedDocument, 1);

        DSSDocument doubleLTADoc = service.extendDocument(extendedDocument, extendParameters);

        // doubleLTADoc.save("target/doubleLTA.cose");

        checkOnSigned(doubleLTADoc, 2);

        Reports reports = verify(doubleLTADoc);

        SimpleReport simpleReport = reports.getSimpleReport();
        assertEquals(Indication.TOTAL_PASSED, simpleReport.getIndication(simpleReport.getFirstSignatureId()));

        DetailedReport detailedReport = reports.getDetailedReport();
        List<String> timestampIds = detailedReport.getTimestampIds();
        assertEquals(3, timestampIds.size());

        DiagnosticData diagnosticData = reports.getDiagnosticData();

        assertEquals(3, diagnosticData.getTimestampList().size());
        TimestampWrapper signatureTst = diagnosticData.getTimestampList().get(0);
        TimestampWrapper firstArchiveTst = diagnosticData.getTimestampList().get(1);
        TimestampWrapper secondArchiveTst = diagnosticData.getTimestampList().get(2);

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        List<RelatedCertificateWrapper> anyValidationDataCertificates = signature.foundCertificates()
                .getRelatedCertificatesByOrigin(CertificateOrigin.ANY_VALIDATION_DATA);
        assertTrue(Utils.isCollectionNotEmpty(anyValidationDataCertificates));

        List<TimestampWrapper> timestampedTimestamps = secondArchiveTst.getTimestampedTimestamps();
        assertEquals(2, timestampedTimestamps.size());
        assertEquals(signatureTst.getId(), timestampedTimestamps.get(0).getId());
        assertEquals(firstArchiveTst.getId(), timestampedTimestamps.get(1).getId());

        List<CertificateWrapper> timestampedCertificates = secondArchiveTst.getTimestampedCertificates();
        List<String> timestampedCertIds = timestampedCertificates.stream().map(CertificateWrapper::getId).collect(Collectors.toList());
        for (CertificateWrapper certificateWrapper : signature.foundCertificates().getRelatedCertificates()) {
            assertTrue(timestampedCertIds.contains(certificateWrapper.getId()));
        }
        for (CertificateWrapper certificateWrapper : signatureTst.foundCertificates().getRelatedCertificates()) {
            assertTrue(timestampedCertIds.contains(certificateWrapper.getId()));
        }
        for (CertificateWrapper certificateWrapper : firstArchiveTst.foundCertificates().getRelatedCertificates()) {
            assertTrue(timestampedCertIds.contains(certificateWrapper.getId()));
        }

        assertEquals(0, firstArchiveTst.foundRevocations().getRelatedRevocationData().size());
        List<RelatedRevocationWrapper> timestampValidationDataRevocations = signature
                .foundRevocations().getRelatedRevocationData();
        assertTrue(Utils.isCollectionNotEmpty(timestampValidationDataRevocations));

        List<RevocationWrapper> timestampedRevocations = secondArchiveTst.getTimestampedRevocations();
        assertEquals(timestampValidationDataRevocations.size(), timestampedRevocations.size());

        List<String> timestampedRevocationIds = timestampedRevocations.stream().map(RevocationWrapper::getId).collect(Collectors.toList());
        for (RevocationWrapper revocationWrapper : timestampValidationDataRevocations) {
            assertTrue(timestampedRevocationIds.contains(revocationWrapper.getId()));
        }

        assertContainsAllRevocationData(signature.getCertificateChain());
        for (TimestampWrapper timestamp : diagnosticData.getTimestampList()) {
            assertContainsAllRevocationData(timestamp.getCertificateChain());
        }
        for (RevocationWrapper revocation : diagnosticData.getAllRevocationData()) {
            assertContainsAllRevocationData(revocation.getCertificateChain());
        }

    }

    @Override
    protected SignedDocumentValidator getValidator(DSSDocument signedDocument) {
        SignedDocumentValidator validator = super.getValidator(signedDocument);
        validator.setTokenExtractionStrategy(TokenExtractionStrategy.EXTRACT_TIMESTAMPS_ONLY);
        return validator;
    }

    private void checkOnSigned(DSSDocument document, int expectedArcTsts) {
        assertTrue(CBORUtils.isCbor(document));

        byte[] binaries = DSSUtils.toByteArray(document);
        CBORObject cborObject = CBORUtils.parseCbor(binaries);
        assertNotNull(cborObject);
        assertTrue(cborObject.isArray());

        CBORArray coseSign1 = (CBORArray) cborObject;
        assertEquals(4, coseSign1.getSize());

        CBORObject protectedHeader = coseSign1.getItem(0);
        assertTrue(protectedHeader.isByteString());

        CBORObject parsedProtectedHeader = CBORUtils.parseCbor(protectedHeader.getValueAsBytes());
        assertNotNull(parsedProtectedHeader);
        assertTrue(parsedProtectedHeader.isMap());
        assertFalse(((CBORMap) parsedProtectedHeader).isEmpty());

        CBORObject unprotectedHeader = coseSign1.getItem(1);
        assertTrue(unprotectedHeader.isMap());

        CBORMap unprotectedHeaderMap = (CBORMap) unprotectedHeader;
        assertFalse(unprotectedHeaderMap.isEmpty());
        assertEquals(1, unprotectedHeaderMap.getSize());

        CBORObject uHeaders = unprotectedHeaderMap.getHeader(COSEHeaderParameter.U_HEADERS.cbor());
        assertNotNull(uHeaders);
        assertTrue(uHeaders.isArray());

        CBORArray uHeadersArray = (CBORArray) uHeaders;
        assertFalse(uHeadersArray.isEmpty());

        int sigTstCounter = 0;
        int valDataCounter = 0;
        int arcTstCounter = 0;

        for (CBORObject uHeadersComponent : uHeadersArray.getValueAsList()) {
            assertTrue(uHeadersComponent.isByteString()); // cbor btsr encoded
            CBORByteString uHeaderBtsr = (CBORByteString) uHeadersComponent;

            CBORObject uHeaderObject = CBORUtils.parseCbor(uHeaderBtsr.getValueAsBytes());
            assertNotNull(uHeaderObject);
            assertTrue(uHeaderObject.isMap());

            CBORMap uHeaderObjectMap =  (CBORMap) uHeaderObject;
            assertFalse(uHeaderObjectMap.isEmpty());
            assertEquals(1, uHeaderObjectMap.getSize());

            CBORObject sigTst  = uHeaderObjectMap.getHeader(COSEHeaderParameter.SIG_TST.cbor());
            if (sigTst != null) {
                ++sigTstCounter;

                CBORMap tstContainer = (CBORMap) sigTst;
                CBORArray tstTokens = tstContainer.getAsArray(COSEHeaderParameter.TST_CONTAINER_TST_TOKENS.cbor());
                assertEquals(1, tstTokens.getSize());
            }
            CBORObject valData  = uHeaderObjectMap.getHeader(COSEHeaderParameter.VAL_DATA.cbor());
            if (valData != null) {
                ++valDataCounter;
            }
            CBORObject arcTst  = uHeaderObjectMap.getHeader(COSEHeaderParameter.ARC_TST.cbor());
            if (arcTst != null) {
                ++arcTstCounter;

                CBORMap tstContainer = (CBORMap) arcTst;
                CBORArray tstTokens = tstContainer.getAsArray(COSEHeaderParameter.TST_CONTAINER_TST_TOKENS.cbor());
                assertEquals(1, tstTokens.getSize());
            }
        }

        assertEquals(1, sigTstCounter);
        assertEquals(expectedArcTsts, arcTstCounter);
        assertEquals(expectedArcTsts > 0 ? expectedArcTsts : 1, valDataCounter);

        CBORObject payload = coseSign1.getItem(2);
        assertTrue(payload.isByteString());

        CBORObject signature = coseSign1.getItem(3);
        assertTrue(signature.isByteString());
    }

    private void assertContainsAllRevocationData(List<CertificateWrapper> certificateChain) {
        for (CertificateWrapper certificate : certificateChain) {
            if (certificate.isTrusted()) {
                break;
            }
            assertTrue(certificate.isRevocationDataAvailable() || certificate.isSelfSigned(),
                    "Certificate with id : [" + certificate.getId() + "] does not have a revocation data!");
        }
    }

    @Override
    protected String getSigningAlias() {
        return RSA_SHA3_USER;
    }

    @Override
    public void validate() {
        // do nothing
    }

    @Override
    protected DSSDocument getSignedDocument() {
        return null;
    }

}
