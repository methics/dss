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

import eu.europa.esig.dss.enumerations.SignatureProfile;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.exception.IllegalInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JAdESExtenderBToLTWithClearEtsiUParamsTest extends JAdESExtenderBToLTATest {

    private SignatureProfile signatureProfile;

    @Override
    protected DSSDocument extendSignature(DSSDocument signedDocument) throws Exception {
        signatureProfile = SignatureProfile.BASELINE_LTA;
        Exception exception = assertThrows(IllegalInputException.class, () -> super.extendSignature(signedDocument));
        assertEquals("Unable to extend JAdES-LTA level. Clear 'etsiU' incorporation requires a canonicalization method!", exception.getMessage());

        signatureProfile = SignatureProfile.BASELINE_LT;
        return super.extendSignature(signedDocument);
    }

    @Override
    protected JAdESSignatureParameters getExtensionParameters() {
        JAdESSignatureParameters extensionParameters = super.getExtensionParameters();
        extensionParameters.setBase64UrlEncodedEtsiUComponents(false);
        return extensionParameters;
    }

    @Override
    protected SignatureProfile getTargetSignatureProfile() {
        return signatureProfile;
    }

}
