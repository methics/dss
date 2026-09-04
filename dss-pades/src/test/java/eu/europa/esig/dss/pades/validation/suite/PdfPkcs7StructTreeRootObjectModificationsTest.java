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
package eu.europa.esig.dss.pades.validation.suite;

import eu.europa.esig.dss.diagnostic.CertificateRefWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.utils.Utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfPkcs7StructTreeRootObjectModificationsTest extends AbstractPAdESTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new InMemoryDocument(getClass().getResourceAsStream("/validation/pdf-structtreeroot-changes.pdf"));
    }

    @Override
    protected void checkPdfRevision(DiagnosticData diagnosticData) {
        super.checkPdfRevision(diagnosticData);

        boolean firstSigFound = false;
        boolean secondSigFound = false;
        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            if (Utils.isCollectionNotEmpty(signatureWrapper.getPdfSignatureOrFormFillChanges())) {
                firstSigFound = true;
                assertFalse(Utils.isCollectionEmpty(signatureWrapper.getPdfExtensionChanges()));
                assertTrue(Utils.isCollectionEmpty(signatureWrapper.getPdfAnnotationChanges()));
                assertTrue(Utils.isCollectionEmpty(signatureWrapper.getPdfUndefinedChanges()));

            } else {
                secondSigFound = true;
                assertTrue(Utils.isCollectionEmpty(signatureWrapper.getPdfExtensionChanges()));
                assertTrue(Utils.isCollectionEmpty(signatureWrapper.getPdfAnnotationChanges()));
                assertTrue(Utils.isCollectionEmpty(signatureWrapper.getPdfUndefinedChanges()));
            }
        }
        assertTrue(firstSigFound);
        assertTrue(secondSigFound);
    }

    @Override
    protected void checkSigningCertificateValue(DiagnosticData diagnosticData) {
        // no signing-certificate
        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            assertFalse(signatureWrapper.isSigningCertificateIdentified());
            assertFalse(signatureWrapper.isSigningCertificateReferencePresent());

            CertificateRefWrapper signingCertificateReference = signatureWrapper.getSigningCertificateReference();
            assertNull(signingCertificateReference);
        }
    }

}
