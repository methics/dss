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
package eu.europa.esig.dss.jades.extension.extender;

import eu.europa.esig.dss.alert.exception.AlertException;
import eu.europa.esig.dss.enumerations.JWSSerializationType;
import eu.europa.esig.dss.enumerations.SigDMechanism;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.enumerations.SignatureProfile;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.spi.extension.DocumentExtender;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JAdESExtenderDetachedBToLTATest extends AbstractTestExtensionWithJAdESDocumentExtender {

    private FileDocument originalDocument;

    private JAdESSignatureParameters signatureParameters;
    private JAdESSignatureParameters extensionParameters;

    @BeforeEach
    void init() {
        signatureParameters = super.getSignatureParameters();
        signatureParameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        signatureParameters.setSigDMechanism(SigDMechanism.OBJECT_ID_BY_URI_HASH);

        extensionParameters = super.getExtensionParameters();
    }

    @Override
    protected SignatureLevel getOriginalSignatureLevel() {
        return SignatureLevel.JAdES_BASELINE_B;
    }

    @Override
    protected SignatureProfile getTargetSignatureProfile() {
        return SignatureProfile.BASELINE_LTA;
    }

    @Override
    protected DSSDocument extendSignature(DSSDocument signedDocument) throws Exception {
        DocumentExtender documentExtender = getDocumentExtender(signedDocument);

        Exception exception = assertThrows(AlertException.class, () -> documentExtender.extendDocument(getTargetSignatureProfile()));
        assertTrue(exception.getMessage().contains("Error on signature augmentation"));

        DSSDocument extendedDocument = documentExtender.extendDocument(getTargetSignatureProfile(), getDetachedContents());
        onDocumentExtended(extendedDocument);

        extensionParameters.setJwsSerializationType(JWSSerializationType.JSON_SERIALIZATION);
        Reports reports = verify(extendedDocument);
        checkFinalLevel(reports.getDiagnosticData());

        extensionParameters.setJwsSerializationType(JWSSerializationType.FLATTENED_JSON_SERIALIZATION);

        extendedDocument = documentExtender.extendDocument(getTargetSignatureProfile(), getDetachedContents(), extensionParameters);
        onDocumentExtended(extendedDocument);
        reports = verify(extendedDocument);
        checkFinalLevel(reports.getDiagnosticData());

        // no detached contents
        extensionParameters.setDetachedContents(null);
        exception = assertThrows(AlertException.class, () -> documentExtender.extendDocument(getTargetSignatureProfile(), extensionParameters));
        assertTrue(exception.getMessage().contains("Error on signature augmentation"));

        extensionParameters.setDetachedContents(getDetachedContents());
        extendedDocument = documentExtender.extendDocument(getTargetSignatureProfile(), extensionParameters);
        onDocumentExtended(extendedDocument);
        reports = verify(extendedDocument);
        checkFinalLevel(reports.getDiagnosticData());

        return extendedDocument;
    }

    @Override
    protected JAdESSignatureParameters getSignatureParameters() {
        return signatureParameters;
    }

    @Override
    public JAdESSignatureParameters getExtensionParameters() {
        return extensionParameters;
    }

    @Override
    protected List<DSSDocument> getDetachedContents() {
        return Collections.singletonList(getOriginalDocument());
    }

    @Override
    public FileDocument getOriginalDocument() {
        if (originalDocument == null) {
            originalDocument = super.getOriginalDocument();
        }
        return originalDocument;
    }

}
