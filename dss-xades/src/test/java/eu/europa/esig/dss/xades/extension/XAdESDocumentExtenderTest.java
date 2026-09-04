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
package eu.europa.esig.dss.xades.extension;

import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.AbstractSerializableSignatureParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.spi.extension.DocumentExtender;
import eu.europa.esig.dss.test.extension.AbstractTestDocumentExtender;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XAdESDocumentExtenderTest extends AbstractTestDocumentExtender {

    @Test
    void isSupported() {
        XAdESDocumentExtender extender = new XAdESDocumentExtender();

        byte[] wrongBytes = new byte[] { 1, 2 };
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test", MimeTypeEnum.PDF)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test")));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test", MimeTypeEnum.XML)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test.xml")));

        assertTrue(extender.isSupported(new InMemoryDocument(new byte[] { '<', '?', 'x', 'm', 'l' })));
        assertTrue(extender.isSupported(new InMemoryDocument(new byte[] { -17, -69, -65, '<' })));
        assertTrue(extender.isSupported(new InMemoryDocument(new byte[] { '<', 'd', 's', ':' })));
    }

    @Override
    protected DocumentExtender initEmptyExtender() {
        return new XAdESDocumentExtender();
    }

    @Override
    protected DocumentExtender initExtender(DSSDocument document) {
        return new XAdESDocumentExtender(document);
    }

    @Override
    protected List<DSSDocument> getValidDocuments() {
        List<DSSDocument> documents = new ArrayList<>();
        documents.add(new FileDocument("src/test/resources/validation/dss-signed.xml"));
        documents.add(new FileDocument("src/test/resources/validation/valid-xades.xml"));
        documents.add(new FileDocument("src/test/resources/validation/xades-x-level.xml"));
        documents.add(new FileDocument("src/test/resources/validation/valid.xades"));
        return documents;
    }

    @Override
    protected DSSDocument getMalformedDocument() {
        return new FileDocument("src/test/resources/validation/malformed-xades.xml");
    }

    @Override
    protected DSSDocument getOtherTypeDocument() {
        return new FileDocument("src/test/resources/sample.png");
    }

    @Override
    protected DSSDocument getSignatureDocument() {
        XAdESService service = new XAdESService(getCompleteCertificateVerifier());
        XAdESSignatureParameters signatureParameters = new XAdESSignatureParameters();
        signatureParameters.setSigningCertificate(getSigningCert());
        signatureParameters.setCertificateChain(getCertificateChain());
        signatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);

        DSSDocument toSignDocument = getNoSignatureDocument();
        ToBeSigned dataToSign = service.getDataToSign(toSignDocument, signatureParameters);
        SignatureValue signatureValue = getToken().sign(dataToSign, signatureParameters.getDigestAlgorithm(), getPrivateKeyEntry());
        return service.signDocument(toSignDocument, signatureParameters, signatureValue);
    }

    @Override
    protected DSSDocument getNoSignatureDocument() {
        return new FileDocument("src/test/resources/sample.xml");
    }

    @Override
    protected DSSDocument getXmlEvidenceRecordDocument() {
        return new FileDocument("src/test/resources/validation/evidence-record/evidence-record-5b5edd31-344d-4d66-8e95-79f9acaab566.xml");
    }

    @Override
    protected SignatureForm getSignatureForm() {
        return SignatureForm.XAdES;
    }

    @Override
    protected AbstractSerializableSignatureParameters<?> initExtensionParameters() {
        return new XAdESSignatureParameters();
    }

    @Override
    protected DocumentSignatureService<?, ?> initService() {
        return new XAdESService(getCompleteCertificateVerifier());
    }

    @Override
    protected String parseErrorMessage() {
        return "An XML file is expected";
    }

    @Override
    protected String noSignatureErrorMessage() {
        return "No signatures found to be extended!";
    }

}
