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
package eu.europa.esig.dss.cbades.extension;

import eu.europa.esig.dss.alert.SilentOnStatusAlert;
import eu.europa.esig.dss.cbades.COSEHeaderParameter;
import eu.europa.esig.dss.cbades.cbor.CBORArray;
import eu.europa.esig.dss.cbades.cbor.CBORByteString;
import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;
import eu.europa.esig.dss.cbades.signature.CBAdESService;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CBAdESExtensionLTToLTUpdateTest extends AbstractCBAdESTestExtension {

    @Override
    protected SignatureLevel getOriginalSignatureLevel() {
        return SignatureLevel.CB_AdES_BASELINE_LT;
    }

    @Override
    protected SignatureLevel getFinalSignatureLevel() {
        return SignatureLevel.CB_AdES_BASELINE_LT;
    }

    @Override
    protected CBAdESService getSignatureServiceToSign() {
        CertificateVerifier certificateVerifier = getCompleteCertificateVerifier();
        certificateVerifier.setCrlSource(null);
        certificateVerifier.setAlertOnMissingRevocationData(new SilentOnStatusAlert());

        CBAdESService service = new CBAdESService(certificateVerifier);
        service.setTspSource(getUsedTSPSourceAtSignatureTime());
        return service;
    }

    @Override
    protected void checkOriginalLevel(DiagnosticData diagnosticData) {
        // no complete revocation data
        assertEquals(SignatureLevel.CB_AdES_BASELINE_T, diagnosticData.getSignatureFormat(diagnosticData.getFirstSignatureId()));
    }

    @Override
    protected DSSDocument extendSignature(DSSDocument signedDocument) throws Exception {
        DSSDocument extendedDocument = super.extendSignature(signedDocument);
        assertTrue(CBORUtils.isCbor(extendedDocument));

        byte[] binaries = DSSUtils.toByteArray(extendedDocument);
        CBORObject cborObject = CBORUtils.parseCbor(binaries);
        assertNotNull(cborObject);
        assertTrue(cborObject.isArray());

        CBORArray coseSign1 = (CBORArray) cborObject;
        assertEquals(4, coseSign1.getSize());

        CBORObject protectedHeader = coseSign1.getItem(0);
        assertTrue(protectedHeader.isByteString());

        CBORObject parsedProtectedHeader = CBORUtils.parseCbor(protectedHeader.getValueAsBytes());
        assertNotNull(parsedProtectedHeader);
        assertTrue(parsedProtectedHeader.isMap());
        assertFalse(((CBORMap) parsedProtectedHeader).isEmpty());

        CBORObject unprotectedHeader = coseSign1.getItem(1);
        assertTrue(unprotectedHeader.isMap());

        CBORMap unprotectedHeaderMap = (CBORMap) unprotectedHeader;
        assertFalse(unprotectedHeaderMap.isEmpty());
        assertEquals(1, unprotectedHeaderMap.getSize());

        CBORObject uHeaders = unprotectedHeaderMap.getHeader(COSEHeaderParameter.U_HEADERS.cbor());
        assertNotNull(uHeaders);
        assertTrue(uHeaders.isArray());

        CBORArray uHeadersArray = (CBORArray) uHeaders;
        assertFalse(uHeadersArray.isEmpty());

        int sigTstCounter = 0;
        int valDataCounter = 0;
        int arcTstCounter = 0;

        boolean xValsFound = false;
        boolean rValsFound = false;

        boolean crlValsFound = false;
        boolean ocspValsFound = false;

        for (CBORObject uHeadersComponent : uHeadersArray.getValueAsList()) {
            assertTrue(uHeadersComponent.isByteString()); // cbor btsr encoded
            CBORByteString uHeaderBtsr = (CBORByteString) uHeadersComponent;

            CBORObject uHeaderObject = CBORUtils.parseCbor(uHeaderBtsr.getValueAsBytes());
            assertNotNull(uHeaderObject);
            assertTrue(uHeaderObject.isMap());

            CBORMap uHeaderObjectMap =  (CBORMap) uHeaderObject;
            assertFalse(uHeaderObjectMap.isEmpty());
            assertEquals(1, uHeaderObjectMap.getSize());

            CBORObject sigTst  = uHeaderObjectMap.getHeader(COSEHeaderParameter.SIG_TST.cbor());
            if (sigTst != null) {
                ++sigTstCounter;

                CBORMap tstContainer = (CBORMap) sigTst;
                CBORArray tstTokens = tstContainer.getAsArray(COSEHeaderParameter.TST_CONTAINER_TST_TOKENS.cbor());
                assertEquals(1, tstTokens.getSize());
            }
            CBORObject valData  = uHeaderObjectMap.getHeader(COSEHeaderParameter.VAL_DATA.cbor());
            if (valData != null) {
                CBORMap valDataMap = (CBORMap) valData;
                if (valDataMap.getAsArray(COSEHeaderParameter.VAL_DATA_X_VALS.cbor()) != null) {
                    xValsFound = true;
                }
                CBORMap rVals = valDataMap.getAsMap(COSEHeaderParameter.VAL_DATA_R_VALS.cbor());
                assertNotNull(rVals);
                rValsFound = true;

                if (rVals.getAsArray(COSEHeaderParameter.R_VALS_CRL_VALS.cbor()) != null) {
                    crlValsFound = true;
                }
                if (rVals.getAsArray(COSEHeaderParameter.R_VALS_OCSP_VALS.cbor()) != null) {
                    ocspValsFound = true;
                }

                ++valDataCounter;
            }
            CBORObject arcTst  = uHeaderObjectMap.getHeader(COSEHeaderParameter.ARC_TST.cbor());
            if (arcTst != null) {
                ++arcTstCounter;
            }
        }

        assertEquals(1, sigTstCounter);
        assertEquals(1, valDataCounter);
        assertEquals(0, arcTstCounter);

        assertTrue(xValsFound);
        assertTrue(rValsFound);
        assertTrue(crlValsFound);
        assertTrue(ocspValsFound);

        return extendedDocument;
    }

    @Override
    protected String getSigningAlias() {
        return GOOD_USER_WITH_CRL_AND_OCSP;
    }

}
