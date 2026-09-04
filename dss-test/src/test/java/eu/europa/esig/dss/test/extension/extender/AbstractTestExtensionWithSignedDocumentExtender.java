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
package eu.europa.esig.dss.test.extension.extender;

import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignatureProfile;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SerializableSignatureParameters;
import eu.europa.esig.dss.model.SerializableTimestampParameters;
import eu.europa.esig.dss.extension.SignedDocumentExtender;
import eu.europa.esig.dss.spi.extension.DocumentExtender;
import eu.europa.esig.dss.test.extension.AbstractTestExtension;

public abstract class AbstractTestExtensionWithSignedDocumentExtender<SP extends SerializableSignatureParameters,
        TP extends SerializableTimestampParameters> extends AbstractTestExtension<SP, TP> {

    @Override
    protected DSSDocument extendSignature(DSSDocument signedDocument) throws Exception {
        DocumentExtender documentExtender = getDocumentExtender(signedDocument);
        return documentExtender.extendDocument(getTargetSignatureProfile(), getDetachedContents(), getExtensionParameters());
    }

    protected DocumentExtender getDocumentExtender(DSSDocument signedDocument) {
        DocumentExtender documentExtender = SignedDocumentExtender.fromDocument(signedDocument);
        documentExtender.setCertificateVerifier(getCompleteCertificateVerifier());
        documentExtender.setTspSource(getUsedTSPSourceAtExtensionTime());
        return documentExtender;
    }

    @Override
    protected SignatureLevel getFinalSignatureLevel() {
        return SignatureLevel.getSignatureLevel(getOriginalSignatureLevel().getSignatureForm(), getTargetSignatureProfile());
    }

    protected abstract SignatureProfile getTargetSignatureProfile();

}
