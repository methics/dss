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
package eu.europa.esig.dss.xades.validation;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XAdESCounterSignatureWithTypeInvalidTest extends AbstractXAdESTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/xades-counter-signature-with-type-invalid.xml");
    }

    @Override
    protected void checkBLevelValid(DiagnosticData diagnosticData) {
        int sigCounter = 0;
        int counterSigCounter = 0;
        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            if (signatureWrapper.isCounterSignature()) {
                assertFalse(signatureWrapper.isBLevelTechnicallyValid());
                assertTrue(signatureWrapper.isSignatureIntact());
                assertFalse(signatureWrapper.isSignatureValid());

                assertEquals(3, signatureWrapper.getDigestMatchers().size());

                int counterSigDMCounter = 0;
                int counterSigSigValueDMCounter = 0;
                int otherRefCounter = 0;
                for (XmlDigestMatcher digestMatcher : signatureWrapper.getDigestMatchers()) {
                    if (DigestMatcherType.COUNTER_SIGNATURE == digestMatcher.getType()) {
                        assertTrue(digestMatcher.isDataFound());
                        assertTrue(digestMatcher.isDataIntact());
                        ++counterSigDMCounter;
                    } else if (DigestMatcherType.COUNTER_SIGNED_SIGNATURE_VALUE == digestMatcher.getType()) {
                        assertTrue(digestMatcher.isDataFound());
                        assertFalse(digestMatcher.isDataIntact());
                        ++counterSigSigValueDMCounter;
                    } else {
                        assertTrue(digestMatcher.isDataFound());
                        assertTrue(digestMatcher.isDataIntact());
                        ++otherRefCounter;
                    }
                }
                assertEquals(1, counterSigDMCounter);
                assertEquals(1, counterSigSigValueDMCounter);
                assertEquals(1, otherRefCounter);

                ++counterSigCounter;
            } else {
                assertTrue(signatureWrapper.isBLevelTechnicallyValid());
                assertTrue(signatureWrapper.isSignatureIntact());
                assertTrue(signatureWrapper.isSignatureValid());

                ++sigCounter;
            }
        }
        assertEquals(1, sigCounter);
        assertEquals(1, counterSigCounter);
    }

    @Override
    protected void checkSignatureLevel(DiagnosticData diagnosticData) {
        super.checkSignatureLevel(diagnosticData);

        int sigCounter = 0;
        int counterSigCounter = 0;

        for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
            if (signatureWrapper.isCounterSignature()) {
                assertEquals(SignatureLevel.XAdES_BASELINE_B, signatureWrapper.getSignatureFormat());
                ++counterSigCounter;
            } else {
                assertEquals(SignatureLevel.XAdES_BES, signatureWrapper.getSignatureFormat());
                ++sigCounter;
            }
        }
        assertEquals(1, sigCounter);
        assertEquals(1, counterSigCounter);
    }

}
