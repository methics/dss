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

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignerData;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.enumerations.SignatureScopeType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.signature.MultipleDocumentsSignatureService;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESTimestampParameters;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XAdESLevelBDetachedMultipleDocumentsNullNamesOnValidationTest extends AbstractXAdESMultipleDocumentsSignatureService {

    private XAdESSignatureParameters signatureParameters;
    private List<DSSDocument> documentToSigns;

    @BeforeEach
    void init() throws Exception {
        documentToSigns = Arrays.asList(
                new FileDocument("src/test/resources/sample.xml"),
                new FileDocument("src/test/resources/sampleWithPlaceOfSignature.xml"),
                new InMemoryDocument(DSSUtils.EMPTY_BYTE_ARRAY, "emptyByteArray")
        );

        signatureParameters = new XAdESSignatureParameters();
        signatureParameters.setSigningCertificate(getSigningCert());
        signatureParameters.setCertificateChain(getCertificateChain());
        signatureParameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
    }

    @Override
    protected List<DSSDocument> getDetachedContents() {
        DSSDocument docOne = new FileDocument("src/test/resources/sample.xml");
        docOne.setName(null);
        DSSDocument docTwo = new FileDocument("src/test/resources/sampleWithPlaceOfSignature.xml");
        docTwo.setName(null);
        DSSDocument docThree = new InMemoryDocument(DSSUtils.EMPTY_BYTE_ARRAY, null);
        return Arrays.asList(docOne, docTwo, docThree);
    }

    @Override
    protected void checkDigestMatchers(DiagnosticData diagnosticData) {
        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        List<XmlDigestMatcher> digestMatchers = signature.getDigestMatchers();
        assertTrue(Utils.isCollectionNotEmpty(digestMatchers));

        int referencesCounter = 0;
        for (XmlDigestMatcher digestMatcher : digestMatchers) {
            assertTrue(digestMatcher.isDataFound());
            assertTrue(digestMatcher.isDataIntact());
            if (DigestMatcherType.REFERENCE == digestMatcher.getType()) {
                assertNotNull(digestMatcher.getUri());
                assertNull(digestMatcher.getDocumentName());
                assertNotEquals(digestMatcher.getUri(), digestMatcher.getDocumentName());
                ++referencesCounter;
            }
            assertFalse(digestMatcher.isDuplicated());
        }
        assertEquals(3, referencesCounter);
    }

    @Override
    protected void checkSignatureScopes(DiagnosticData diagnosticData) {
        super.checkSignatureScopes(diagnosticData);

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        List<XmlSignatureScope> signatureScopes = signature.getSignatureScopes();
        assertEquals(3, signatureScopes.size());

        Set<String> signerDataIds = new HashSet<>();
        for (XmlSignatureScope signatureScope : signatureScopes) {
            assertNotNull(signatureScope.getName()); // obtained from initial references
            assertEquals(SignatureScopeType.FULL, signatureScope.getScope());

            XmlSignerData signerData = signatureScope.getSignerData();
            assertNotNull(signerData);
            assertNotNull(signerData.getId());
            assertFalse(signerDataIds.contains(signerData.getId()));
            signerDataIds.add(signerData.getId());
        }
    }

    @Override
    protected void verifyOriginalDocuments(SignedDocumentValidator validator, DiagnosticData diagnosticData) {
        List<DSSDocument> retrievedDocuments = validator.getOriginalDocuments(diagnosticData.getFirstSignatureId());
        for (DSSDocument document : documentToSigns) {
            boolean found = false;
            for (DSSDocument retrievedDoc : retrievedDocuments) {
                if (Arrays.equals(DSSUtils.toByteArray(document), DSSUtils.toByteArray(retrievedDoc))) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Override
    protected MultipleDocumentsSignatureService<XAdESSignatureParameters, XAdESTimestampParameters> getService() {
        return new XAdESService(getOfflineCertificateVerifier());
    }

    @Override
    protected XAdESSignatureParameters getSignatureParameters() {
        return signatureParameters;
    }

    @Override
    protected List<DSSDocument> getDocumentsToSign() {
        return documentToSigns;
    }

}
