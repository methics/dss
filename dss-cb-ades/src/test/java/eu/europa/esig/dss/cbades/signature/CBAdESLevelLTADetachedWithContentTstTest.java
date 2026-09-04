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

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.COSEStructureType;
import eu.europa.esig.dss.enumerations.SigDMechanism;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.signature.MultipleDocumentsSignatureService;
import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CBAdESLevelLTADetachedWithContentTstTest extends AbstractCBAdESMultipleDocumentSignatureTest {

    private CBAdESService service;
    private List<DSSDocument> documentsToSign;
    private Date signingDate;

    @BeforeEach
    void init() throws Exception {
        service = new CBAdESService(getCompleteCertificateVerifier());
        service.setTspSource(getGoodTsa());
        documentsToSign = new ArrayList<>();
        documentsToSign.add(new InMemoryDocument("Hello World!".getBytes(), "HelloWorld"));
        documentsToSign.add(new InMemoryDocument("Bye World!".getBytes(), "ByeWorld"));
        signingDate = new Date();
    }

    @Override
    protected CBAdESSignatureParameters getSignatureParameters() {
        CBAdESSignatureParameters signatureParameters = new CBAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(signingDate);
        signatureParameters.setSigningCertificate(getSigningCert());
        signatureParameters.setCertificateChain(getCertificateChain());
        signatureParameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        signatureParameters.setSigDMechanism(SigDMechanism.OBJECT_ID_BY_URI_HASH);
        signatureParameters.setSignatureLevel(SignatureLevel.CB_AdES_BASELINE_LTA);
        signatureParameters.setCoseStructureType(COSEStructureType.COSE_SIGN1);

        TimestampToken contentTimestamp = service.getContentTimestamp(documentsToSign, signatureParameters);
        signatureParameters.setContentTimestamps(Collections.singletonList(contentTimestamp));

        return signatureParameters;
    }

    @Override
    protected void checkTimestamps(DiagnosticData diagnosticData) {
        super.checkTimestamps(diagnosticData);
        assertEquals(3, diagnosticData.getTimestampList().size());

        boolean contentTstFound = false;
        boolean archiveTstFound = false;
        for (TimestampWrapper timestampWrapper : diagnosticData.getTimestampList()) {
            if (timestampWrapper.getType().isContentTimestamp()) {
                assertEquals(2, timestampWrapper.getTimestampedSignedData().size());
                contentTstFound = true;
            }
            if (timestampWrapper.getType().isArchivalTimestamp()) {
                assertEquals(2, timestampWrapper.getTimestampedSignedData().size());
                archiveTstFound = true;
            }
        }
        assertTrue(contentTstFound);
        assertTrue(archiveTstFound);
    }

    @Override
    protected void checkSignatureScopes(DiagnosticData diagnosticData) {
        super.checkSignatureScopes(diagnosticData);

        assertEquals(2, diagnosticData.getOriginalSignerDocuments().size());
    }

    @Override
    protected List<DSSDocument> getDetachedContents() {
        return documentsToSign;
    }

    @Override
    protected List<DSSDocument> getDocumentsToSign() {
        return documentsToSign;
    }

    @Override
    protected MultipleDocumentsSignatureService<CBAdESSignatureParameters, CBAdESTimestampParameters> getService() {
        return service;
    }

    @Override
    protected String getSigningAlias() {
        return GOOD_USER;
    }

}
