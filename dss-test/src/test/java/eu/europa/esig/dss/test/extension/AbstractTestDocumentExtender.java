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
package eu.europa.esig.dss.test.extension;

import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignatureProfile;
import eu.europa.esig.dss.extension.SignedDocumentExtender;
import eu.europa.esig.dss.model.AbstractSerializableSignatureParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SerializableSignatureParameters;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.exception.IllegalInputException;
import eu.europa.esig.dss.spi.extension.DocumentExtender;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.test.PKIFactoryAccess;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractTestDocumentExtender extends PKIFactoryAccess {

    protected abstract DocumentExtender initEmptyExtender();

    protected abstract DocumentExtender initExtender(DSSDocument document);

    protected abstract List<DSSDocument> getValidDocuments();

    protected abstract DSSDocument getMalformedDocument();

    protected abstract DSSDocument getOtherTypeDocument();

    protected abstract DSSDocument getSignatureDocument();

    protected abstract DSSDocument getNoSignatureDocument();

    protected DSSDocument getBinaryDocument() {
        return new InMemoryDocument(new byte[] { '1', '2', '3' });
    }

    protected abstract DSSDocument getXmlEvidenceRecordDocument();

    @Test
    public void extendSignaturesProfileOnly() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);
        extendAndValidate(extender, getTargetSignatureProfile());
    }

    @Test
    public void extendSignaturesWithExtensionParameters() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);
        AbstractSerializableSignatureParameters<?> extensionParameters = initExtensionParameters();
        extensionParameters.setSignatureLevel(getFinalSignatureLevel());
        extendAndValidate(extender, getTargetSignatureProfile(), extensionParameters);
    }

    @Test
    public void extendSignaturesWithExtensionParametersDiffLevel() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);
        AbstractSerializableSignatureParameters<?> extensionParameters = initExtensionParameters();
        extensionParameters.setSignatureLevel(getFinalSignatureLevel());
        extendAndValidate(extender, SignatureProfile.BASELINE_T, extensionParameters);
    }

    @Test
    public void extendSignaturesDoubleExtensionParams() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);
        AbstractSerializableSignatureParameters<?> extensionParameters = initExtensionParameters();
        extensionParameters.setSignatureLevel(getFinalSignatureLevel());
        extendAndValidate(extender, getTargetSignatureProfile(), extensionParameters, initExtensionParameters());
    }

    @Test
    public void extendSignaturesWithExtensionParametersNoSignatureLevel() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);
        extendAndValidate(extender, getTargetSignatureProfile(), initExtensionParameters());
    }

    @Test
    public void extendSignaturesNullProfile() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);
        Exception exception = assertThrows(NullPointerException.class, () -> extendAndValidate(extender, null));
        assertEquals("SignatureProfile cannot be null!", exception.getMessage());
    }

    @Test
    public void extendSignaturesBProfile() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> extendAndValidate(extender, SignatureProfile.BASELINE_B));
        SignatureLevel signatureLevel = SignatureLevel.getSignatureLevel(getSignatureForm(), SignatureProfile.BASELINE_B);
        assertEquals(String.format("Unsupported signature format '%s' for extension.", signatureLevel), exception.getMessage());
    }

    @Test
    public void extendSignaturesImpossibleProfile() {
        DSSDocument document = getSignatureDocument();
        DocumentExtender extender = initExtender(document);
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> extendAndValidate(extender, SignatureProfile.NOT_ETSI));
        SignatureLevel signatureLevel = SignatureLevel.getSignatureLevel(getSignatureForm(), SignatureProfile.NOT_ETSI);
        assertEquals(String.format("Unsupported signature format '%s' for extension.", signatureLevel), exception.getMessage());
    }

    @Test
    public void binaryDocumentExtension() {
        DSSDocument document = getBinaryDocument();
        DocumentExtender extender = initExtender(document);
        Exception exception = assertThrows(IllegalInputException.class, () -> extendAndValidate(extender, getTargetSignatureProfile()));
        assertTrue(exception.getMessage().contains(parseErrorMessage()), exception.getMessage());
    }

    @Test
    public void malformedDocumentExtension() {
        DSSDocument document = getMalformedDocument();
        DocumentExtender extender = initExtender(document);
        Exception exception = assertThrows(IllegalInputException.class, () -> extendAndValidate(extender, getTargetSignatureProfile()));
        assertTrue(exception.getMessage().contains(parseErrorMessage()), exception.getMessage());
    }

    @Test
    public void otherDocumentTypeExtension() {
        DSSDocument document = getOtherTypeDocument();
        DocumentExtender extender = initExtender(document);
        Exception exception = assertThrows(IllegalInputException.class, () -> extendAndValidate(extender, getTargetSignatureProfile()));
        assertTrue(exception.getMessage().contains(parseErrorMessage()), exception.getMessage());
    }

    @Test
    public void noSignatureDocumentExtension() {
        DSSDocument document = getNoSignatureDocument();
        DocumentExtender extender = initExtender(document);
        Exception exception = assertThrows(IllegalInputException.class, () -> extendAndValidate(extender, getTargetSignatureProfile()));
        assertTrue(exception.getMessage().contains(noSignatureErrorMessage()), exception.getMessage());
    }

    protected abstract String parseErrorMessage();

    protected abstract String noSignatureErrorMessage();

    @Test
    public void isSupportedValidDocument() {
        List<DSSDocument> documents = getValidDocuments();
        for (DSSDocument document : documents) {
            assertTrue(initEmptyExtender().isSupported(document));
        }
    }

    @Test
    public void isSupportedBinaryDocument() {
        assertFalse(initEmptyExtender().isSupported(getBinaryDocument()));
    }

    @Test
    public void isSupportedMalformedDocument() {
        assertFalse(initEmptyExtender().isSupported(getMalformedDocument()));
    }

    @Test
    public void isSupportedOtherTypeDocument() {
        assertFalse(initEmptyExtender().isSupported(getOtherTypeDocument()));
    }

    @Test
    public void isSupportedNoSignatureDocument() {
        DSSDocument document = getNoSignatureDocument();
        if (document != null) {
            assertTrue(initEmptyExtender().isSupported(document));
        }
    }

    @Test
    public void isSupportedEvidenceRecordDocument() {
        DSSDocument document = getXmlEvidenceRecordDocument();
        if (document != null) {
            assertFalse(initEmptyExtender().isSupported(document));
        }
    }

    @Test
    public void nullDocumentProvided() {
        Exception exception = assertThrows(NullPointerException.class, () -> initExtender(null));
        assertEquals("Document to be extended cannot be null!", exception.getMessage());
    }

    @Test
    public void nullFromDocument() {
        Exception exception = assertThrows(NullPointerException.class, () ->  SignedDocumentExtender.fromDocument(null));
        assertEquals("DSSDocument is null", exception.getMessage());
    }

    @Test
    public void customServiceTest() {
        DSSDocument signatureDocument = getSignatureDocument();
        DocumentSignatureService<?, ?> service = initService();

        SignedDocumentExtender extender = SignedDocumentExtender.fromDocument(signatureDocument);
        Exception exception = assertThrows(NullPointerException.class, () ->  extender.extendDocument(getTargetSignatureProfile()));
        assertEquals(String.format("Please provide CertificateVerifier or corresponding %s!", service.getClass().getSimpleName()), exception.getMessage());

        extender.setServices(service);

        exception = assertThrows(NullPointerException.class, () ->  extender.extendDocument(getTargetSignatureProfile()));
        assertEquals("The TSPSource cannot be null", exception.getMessage());

        service.setTspSource(getGoodTsa());

        DSSDocument extendedDocument = extender.extendDocument(getTargetSignatureProfile());
        validate(extendedDocument);
    }

    protected void extendAndValidate(DocumentExtender extender, SignatureProfile signatureProfile,
                                     SerializableSignatureParameters... signatureParameters) {
        extender.setCertificateVerifier(getCompleteCertificateVerifier());
        extender.setTspSource(getGoodTsa());

        DSSDocument extendedDocument = extender.extendDocument(signatureProfile, signatureParameters);
        validate(extendedDocument);
    }

    protected void validate(DSSDocument extendedDocument) {
        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(extendedDocument);
        validator.setCertificateVerifier(new CommonCertificateVerifier());

        Reports reports = validator.validateDocument();
        assertNotNull(reports);
        SimpleReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        for (String signatureId : simpleReport.getSignatureIdList()) {
            assertEquals(getFinalSignatureLevel(), simpleReport.getSignatureFormat(signatureId));
        }
    }

    protected SignatureLevel getFinalSignatureLevel() {
        return SignatureLevel.getSignatureLevel(getSignatureForm(), getTargetSignatureProfile());
    }

    protected abstract SignatureForm getSignatureForm();

    protected SignatureProfile getTargetSignatureProfile() {
        return SignatureProfile.BASELINE_LTA;
    }

    protected abstract AbstractSerializableSignatureParameters<?> initExtensionParameters();

    protected abstract DocumentSignatureService<?,?> initService();

    @Override
    protected String getSigningAlias() {
        return GOOD_USER;
    }

}
