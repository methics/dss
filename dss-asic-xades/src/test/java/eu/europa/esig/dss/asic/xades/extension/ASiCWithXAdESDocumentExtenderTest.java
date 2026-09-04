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
package eu.europa.esig.dss.asic.xades.extension;

import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
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

class ASiCWithXAdESDocumentExtenderTest extends AbstractTestDocumentExtender {

    @Test
    void isSupported() {
        ASiCWithXAdESDocumentExtender extender = new ASiCWithXAdESDocumentExtender();

        byte[] wrongBytes = new byte[] { 1, 2 };
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test", MimeTypeEnum.PDF)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test")));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test", MimeTypeEnum.XML)));
        assertFalse(extender.isSupported(new InMemoryDocument(wrongBytes, "test.xml")));

        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/onefile-ok.asice")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/onefile-ok.asics")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/multifiles-ok.asice")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/multifiles-ok.asics")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/libreoffice.ods")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/libreoffice.odt")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/open-document-signed.odt")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/open-document-resigned.odt")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/xades-lt-with-er.sce")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/xades-lta-with-er-hashtree.scs")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/xades-lta-with-er-hashtree.sce")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/er-one-file.scs")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/validation/evidencerecord/er-multi-files.sce")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/signable/asic_xades.zip")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/test.zip")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/empty.zip")));
        assertTrue(extender.isSupported(new FileDocument("src/test/resources/ASiCEWith2Signatures.bdoc")));

        assertFalse(extender.isSupported(new FileDocument("src/test/resources/bdoc-spec21.pdf")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/manifest-sample.xml")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/test.txt")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/asic_cades.zip")));
        assertFalse(extender.isSupported(new FileDocument("src/test/resources/signable/asic_cades_er.sce")));
    }

    @Override
    protected DocumentExtender initEmptyExtender() {
        return new ASiCWithXAdESDocumentExtender();
    }

    @Override
    protected DocumentExtender initExtender(DSSDocument document) {
        return new ASiCWithXAdESDocumentExtender(document);
    }

    @Override
    protected List<DSSDocument> getValidDocuments() {
        List<DSSDocument> documents = new ArrayList<>();
        documents.add(new FileDocument("src/test/resources/validation/onefile-ok.asice"));
        documents.add(new FileDocument("src/test/resources/validation/onefile-ok.asics"));
        documents.add(new FileDocument("src/test/resources/validation/multifiles-ok.asice"));
        documents.add(new FileDocument("src/test/resources/validation/multifiles-ok.asics"));
        documents.add(new FileDocument("src/test/resources/validation/libreoffice.ods"));
        documents.add(new FileDocument("src/test/resources/validation/libreoffice.odt"));
        documents.add(new FileDocument("src/test/resources/validation/open-document-signed.odt"));
        documents.add(new FileDocument("src/test/resources/validation/open-document-resigned.odt"));
        documents.add(new FileDocument("src/test/resources/validation/evidencerecord/xades-lt-with-er.sce"));
        documents.add(new FileDocument("src/test/resources/signable/asic_xades.zip"));
        return documents;
    }

    @Override
    protected DSSDocument getMalformedDocument() {
        return new FileDocument("src/test/resources/validation/malformed-container.asice");
    }

    @Override
    protected DSSDocument getOtherTypeDocument() {
        return new FileDocument("src/test/resources/manifest-sample.xml");
    }

    @Override
    protected DSSDocument getSignatureDocument() {
        ASiCWithXAdESService service = new ASiCWithXAdESService(getCompleteCertificateVerifier());
        ASiCWithXAdESSignatureParameters signatureParameters = new ASiCWithXAdESSignatureParameters();
        signatureParameters.setSigningCertificate(getSigningCert());
        signatureParameters.setCertificateChain(getCertificateChain());
        signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
        signatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_S);

        DSSDocument toSignDocument = getNoSignatureDocument();
        ToBeSigned dataToSign = service.getDataToSign(toSignDocument, signatureParameters);
        SignatureValue signatureValue = getToken().sign(dataToSign, signatureParameters.getDigestAlgorithm(), getPrivateKeyEntry());
        return service.signDocument(toSignDocument, signatureParameters, signatureValue);
    }

    @Override
    protected DSSDocument getNoSignatureDocument() {
        return new FileDocument("src/test/resources/validation/no-signature.asics");
    }

    @Override
    protected DSSDocument getXmlEvidenceRecordDocument() {
        return new FileDocument("src/test/resources/validation/evidencerecord/incorporation/evidence-record-asic_xades.xml");
    }

    @Override
    protected SignatureForm getSignatureForm() {
        return SignatureForm.XAdES;
    }

    @Override
    protected AbstractSerializableSignatureParameters<?> initExtensionParameters() {
        return new ASiCWithXAdESSignatureParameters();
    }

    @Override
    protected DocumentSignatureService<?, ?> initService() {
        return new ASiCWithXAdESService(getCompleteCertificateVerifier());
    }

    @Override
    protected String parseErrorMessage() {
        return "The provided file is not ASiC document!";
    }

    @Override
    protected String noSignatureErrorMessage() {
        return "No signatures found to be extended!";
    }

}
