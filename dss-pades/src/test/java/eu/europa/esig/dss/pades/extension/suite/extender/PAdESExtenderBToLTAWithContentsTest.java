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
package eu.europa.esig.dss.pades.extension.suite.extender;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PAdESExtenderBToLTAWithContentsTest extends PAdESExtenderBToLTATest {

    private int contentSize;

    @Override
    protected PAdESSignatureParameters getExtensionParameters() {
        PAdESSignatureParameters extensionParameters = super.getExtensionParameters();
        extensionParameters.getSignatureTimestampParameters().setContentSize(contentSize);
        return extensionParameters;
    }

    @Override
    protected DSSDocument extendSignature(DSSDocument signedDocument) throws Exception {
        contentSize = 2; // too small
        Exception exception = assertThrows(IllegalArgumentException.class, () -> super.extendSignature(signedDocument));
        assertTrue(exception.getMessage().contains("Unable to save a document. Reason : The signature size [2] is too small"));

        contentSize = 18944;
        return super.extendSignature(signedDocument);
    }

}
