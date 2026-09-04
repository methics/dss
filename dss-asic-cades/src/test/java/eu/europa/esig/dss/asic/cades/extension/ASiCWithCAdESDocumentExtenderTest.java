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
package eu.europa.esig.dss.asic.cades.extension;

import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.AbstractSerializableSignatureParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.spi.extension.DocumentExtender;
import eu.europa.esig.dss.test.extension.AbstractTestDocumentExtender;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ASiCWithCAdESDocumentExtenderTest extends AbstractTestDocumentExtender {

    @Test
    void isSupported() {
        ASiCWithCAdESDocumentExtender extender = new ASiCWithCAdESDocumentExtender();

        byte[] wrongBytes = new byte[] { 1, 2 };
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test", MimeTypeEnum.PDF)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test")));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test", MimeTypeEnum.XML)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test.p7c")));

        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/onefile-ok.asice")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/onefile-ok.asics")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/multifiles-ok.asice")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/multifiles-ok.asics")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/cades-lt-with-er.sce")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/er-one-file.asics")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/er-multi-files.asice")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/signable/asic_cades.zip")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/test.zip")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/empty.zip")));

        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signature-policy.der")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/test.txt")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/asic_xades.zip")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/document.odt")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/asic_xades_er.sce")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/asic_xades_er.scs")));
    }

    @Override
    protected DocumentExtender initEmptyExtender() {
        return new ASiCWithCAdESDocumentExtender();
    }

    @Override
    protected DocumentExtender initExtender(DSSDocument document) {
        return new ASiCWithCAdESDocumentExtender(document);
    }

    @Override
    protected List<DSSDocument> getValidDocuments() {
        List<DSSDocument> documents = new ArrayList<>();
        documents.add(new FileDocument("src/test/resources/validation/onefile-ok.asice"));
        documents.add(new FileDocument("src/test/resources/validation/onefile-ok.asics"));
        documents.add(new FileDocument("src/test/resources/validation/multifiles-ok.asice"));
        documents.add(new FileDocument("src/test/resources/validation/multifiles-ok.asics"));
        documents.add(new FileDocument("src/test/resources/validation/evidencerecord/cades-lt-with-er.sce"));
        documents.add(new FileDocument("src/test/resources/signable/asic_cades.zip"));
        return documents;
    }

    @Override
    protected DSSDocument getMalformedDocument() {
        return new FileDocument("src/test/resources/validation/malformed-container.asics");
    }

    @Override
    protected DSSDocument getOtherTypeDocument() {
        return new FileDocument("src/test/resources/signable/test.txt");
    }

    @Override
    protected DSSDocument getSignatureDocument() {
        ASiCWithCAdESService service = new ASiCWithCAdESService(getCompleteCertificateVerifier());
        ASiCWithCAdESSignatureParameters signatureParameters = new ASiCWithCAdESSignatureParameters();
        signatureParameters.setSigningCertificate(getSigningCert());
        signatureParameters.setCertificateChain(getCertificateChain());
        signatureParameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_B);
        signatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);

        DSSDocument toSignDocument = getNoSignatureDocument();
        ToBeSigned dataToSign = service.getDataToSign(toSignDocument, signatureParameters);
        SignatureValue signatureValue = getToken().sign(dataToSign, signatureParameters.getDigestAlgorithm(), getPrivateKeyEntry());
        return service.signDocument(toSignDocument, signatureParameters, signatureValue);
    }

    @Override
    protected DSSDocument getNoSignatureDocument() {
        return new FileDocument("src/test/resources/validation/evidencerecord/er-asn1-one-file.asice");
    }

    @Override
    protected DSSDocument getXmlEvidenceRecordDocument() {
        return new FileDocument("src/test/resources/validation/evidencerecord/incorporation/evidence-record-onefile-ok.xml");
    }

    @Override
    protected SignatureForm getSignatureForm() {
        return SignatureForm.CAdES;
    }

    @Override
    protected AbstractSerializableSignatureParameters<?> initExtensionParameters() {
        return new ASiCWithCAdESSignatureParameters();
    }

    @Override
    protected DocumentSignatureService<?, ?> initService() {
        return new ASiCWithCAdESService(getCompleteCertificateVerifier());
    }

    @Override
    protected String parseErrorMessage() {
        return "The provided file is not ASiC document!";
    }

    @Override
    protected String noSignatureErrorMessage() {
        return "No supported signature documents found!";
    }

}
