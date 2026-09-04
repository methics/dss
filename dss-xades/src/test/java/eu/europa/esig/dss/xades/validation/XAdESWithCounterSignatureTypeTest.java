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
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.enumerations.SignatureScopeType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XAdESWithCounterSignatureTypeTest extends AbstractXAdESTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/xades-with-counter-sig-type.xml");
    }

    @Override
    protected void checkBLevelValid(DiagnosticData diagnosticData) {
        super.checkBLevelValid(diagnosticData);

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        assertFalse(signature.isCounterSignature());

        assertTrue(signature.isBLevelTechnicallyValid());
        assertTrue(signature.isSignatureIntact());
        assertTrue(signature.isSignatureValid());

        assertEquals(2, signature.getDigestMatchers().size());

        int counterSigDMCounter = 0;
        int counterSigSigValueDMCounter = 0;
        int otherRefCounter = 0;
        for (XmlDigestMatcher digestMatcher : signature.getDigestMatchers()) {
            if (DigestMatcherType.COUNTER_SIGNATURE == digestMatcher.getType()) {
                ++counterSigDMCounter;
            } else if (DigestMatcherType.COUNTER_SIGNED_SIGNATURE_VALUE == digestMatcher.getType()) {
                ++counterSigSigValueDMCounter;
            } else {
                assertTrue(digestMatcher.isDataFound());
                assertTrue(digestMatcher.isDataIntact());
                ++otherRefCounter;
            }
        }
        assertEquals(0, counterSigDMCounter);
        assertEquals(0, counterSigSigValueDMCounter);
        assertEquals(2, otherRefCounter);
    }

    @Override
    protected void checkSignatureScopes(DiagnosticData diagnosticData) {
        super.checkSignatureScopes(diagnosticData);

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());

        boolean hasCounterSignatureScope = false;

        assertTrue(Utils.isCollectionNotEmpty(signature.getSignatureScopes()));
        for (XmlSignatureScope signatureScope : signature.getSignatureScopes()) {
            assertNotNull(signatureScope.getScope());
            assertNotNull(signatureScope.getSignerData());
            assertNotNull(signatureScope.getSignerData().getDigestAlgoAndValue());
            assertNotNull(signatureScope.getSignerData().getDigestAlgoAndValue().getDigestMethod());
            assertNotNull(signatureScope.getSignerData().getDigestAlgoAndValue().getDigestValue());

            if (SignatureScopeType.COUNTER_SIGNATURE.equals(signatureScope.getScope())) {
                hasCounterSignatureScope = true;
            }
        }

        assertFalse(hasCounterSignatureScope);
    }

}
