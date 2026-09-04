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
package eu.europa.esig.dss.cbades.requirements;

import eu.europa.esig.dss.cbades.cbor.CBORArray;
import eu.europa.esig.dss.cbades.cbor.CBORByteString;
import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;
import eu.europa.esig.dss.utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractCBAdESCoseSignRequirementsCheck extends AbstractCBAdESRequirementsCheck {

    @Override
    protected void onDocumentSigned(byte[] byteArray) {
        super.onDocumentSigned(byteArray);

        try {
            CBORByteString protectedHeader = getBodyProtectedHeader(byteArray);
            checkBodyProtectedHeader(protectedHeader);

            CBORMap unprotectedHeader = getBodyUnprotectedHeader(byteArray);
            checkBodyUnprotectedHeader(unprotectedHeader);

        } catch (Exception e) {
            fail(e);
        }
    }

    @Override
    protected CBORByteString getPayload(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORObject payloadObject = cose.getValueAsList().get(2);
        assertTrue(payloadObject.isByteString());

        return (CBORByteString) payloadObject;
    }

    protected CBORByteString getBodyProtectedHeader(byte[] byteArray) {
        CBORArray cose = getCose(byteArray);
        CBORObject protectedHeader = cose.getValueAsList().get(0);
        assertTrue(protectedHeader.isByteString());

        return (CBORByteString) protectedHeader;
    }

    @Override
    protected CBORByteString getProtectedHeader(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORArray coseSignature = getCoseSignature(cose);
        CBORObject protectedHeader = coseSignature.getValueAsList().get(0);
        assertTrue(protectedHeader.isByteString());

        return (CBORByteString) protectedHeader;
    }

    @Override
    protected CBORByteString getSignatureValue(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORArray coseSignature = getCoseSignature(cose);
        CBORObject signature = coseSignature.getValueAsList().get(2);
        assertTrue(signature.isByteString());

        return (CBORByteString) signature;
    }

    protected CBORMap getBodyUnprotectedHeader(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORObject unprotectedHeader = cose.getValueAsList().get(1);
        assertTrue(unprotectedHeader.isMap());

        return (CBORMap) unprotectedHeader;
    }

    @Override
    protected CBORMap getUnprotectedHeader(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORArray coseSignature = getCoseSignature(cose);
        CBORObject unprotectedHeader = coseSignature.getValueAsList().get(1);
        assertTrue(unprotectedHeader.isMap());

        return (CBORMap) unprotectedHeader;
    }

    private CBORArray getCose(byte[] byteArray) {
        CBORObject cborObject = CBORUtils.parseCbor(byteArray);
        assertTrue(cborObject.isArray());
        CBORArray cborArray = (CBORArray) cborObject;
        assertEquals(4, cborArray.getSize());
        return cborArray;
    }

    private CBORArray getCoseSignature(CBORArray cose) {
        CBORObject signatures = cose.getValueAsList().get(3);
        assertTrue(signatures.isArray());

        CBORArray signaturesArray = (CBORArray) signatures;
        assertEquals(1, signaturesArray.getSize());

        CBORArray signature = signaturesArray.getAsArray(0);
        assertNotNull(signature);
        assertEquals(3, signature.getSize());
        return signature;
    }

    protected void checkBodyProtectedHeader(CBORByteString bodyProtectedHeader) {
        assertNotNull(bodyProtectedHeader);
        assertTrue(Utils.isArrayNotEmpty(bodyProtectedHeader.getValueAsBytes()));

        CBORObject protectedHeaderObject = CBORUtils.parseCbor(bodyProtectedHeader.getValueAsBytes());
        assertTrue(protectedHeaderObject.isMap());

        CBORMap protectedHeaderMap = (CBORMap) protectedHeaderObject;
        assertTrue(protectedHeaderMap.isEmpty());
    }

    protected void checkBodyUnprotectedHeader(CBORMap bodyUnprotectedHeader) {
        assertNotNull(bodyUnprotectedHeader);
        assertTrue(bodyUnprotectedHeader.isEmpty());
    }

}
