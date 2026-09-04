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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractCBAdESCoseSign1RequirementsCheck extends AbstractCBAdESRequirementsCheck {
    
    @Override
    protected CBORByteString getPayload(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORObject payloadObject = cose.getValueAsList().get(2);
        assertTrue(payloadObject.isByteString());

        return (CBORByteString) payloadObject;
    }

    @Override
    protected CBORByteString getProtectedHeader(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORObject protectedHeader = cose.getValueAsList().get(0);
        assertTrue(protectedHeader.isByteString());

        return (CBORByteString) protectedHeader;
    }

    @Override
    protected CBORByteString getSignatureValue(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORObject signature = cose.getValueAsList().get(3);
        assertTrue(signature.isByteString());

        return (CBORByteString) signature;
    }

    @Override
    protected CBORMap getUnprotectedHeader(byte[] byteArray) throws Exception {
        CBORArray cose = getCose(byteArray);
        CBORObject unprotectedHeader = cose.getValueAsList().get(1);
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
    
}
