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
package eu.europa.esig.dss.cbades.signature;

import eu.europa.esig.dss.cbades.validation.AbstractCBAdESTestValidation;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.validation.DocumentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CBAdESCoseSign1NestedCounterSignature0V2Test extends AbstractCBAdESTestValidation {

    private CBAdESService service;
    private DSSDocument signedDocument;
    private CBAdESCounterSignatureParameters counterSignatureParameters;

    @BeforeEach
    void init() {
        signedDocument = new FileDocument("src/test/resources/validation/cb-ades-cosesign1-countersignature0V2-rfc9338.cose");

        service = new CBAdESService(getCompleteCertificateVerifier());

        counterSignatureParameters = new CBAdESCounterSignatureParameters();
        counterSignatureParameters.bLevel().setSigningDate(new Date());
        counterSignatureParameters.setSigningCertificate(getSigningCert());
        counterSignatureParameters.setCertificateChain(getCertificateChain());
        counterSignatureParameters.setSignatureLevel(SignatureLevel.CB_AdES_BASELINE_B);
    }

    @Test
    @Override
    public void validate() {
        DocumentValidator validator = getValidator(signedDocument);

        List<AdvancedSignature> signatures = validator.getSignatures();
        assertEquals(1, signatures.size());

        AdvancedSignature advancedSignature = signatures.get(0);
        List<AdvancedSignature> counterSignatures = advancedSignature.getCounterSignatures();
        assertEquals(1, counterSignatures.size());

        AdvancedSignature counterSignature = counterSignatures.get(0);
        assertNotNull(counterSignature.getMasterSignature());
        assertEquals(0, counterSignature.getCounterSignatures().size());

        counterSignatureParameters.bLevel().setSigningDate(new Date());
        counterSignatureParameters.setSignatureIdToCounterSign(counterSignature.getId());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.getDataToBeCounterSigned(signedDocument, counterSignatureParameters));
        assertEquals("The counter signing of a signature type 'COSE_Countersignature0_V2' is not supported!", exception.getMessage());
    }

    @Override
    protected DSSDocument getSignedDocument() {
        return null;
    }

    @Override
    protected String getSigningAlias() {
        return GOOD_USER;
    }

}
