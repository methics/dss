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

import eu.europa.esig.dss.cbades.signature.CBAdESService;
import eu.europa.esig.dss.cbades.signature.CBAdESSignatureParameters;
import eu.europa.esig.dss.enumerations.COSEStructureType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SigDMechanism;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DigestDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.test.PKIFactoryAccess;
import eu.europa.esig.dss.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CBAdESImpossibleExtensionTest extends PKIFactoryAccess {

    @Test
    void digestDocumentWithLTALevelTest() {
        DSSDocument doc = new InMemoryDocument("Hello world!".getBytes());
        DigestDocument digestDocument = new DigestDocument(DigestAlgorithm.SHA512,
                Utils.toBase64(DSSUtils.digest(DigestAlgorithm.SHA512, doc)), "sample");

        CBAdESService service = new CBAdESService(getCompleteCertificateVerifier());
        service.setTspSource(getGoodTsa());

        CBAdESSignatureParameters parameters = new CBAdESSignatureParameters();
        parameters.setSigningCertificate(getSigningCert());
        parameters.setCertificateChain(getCertificateChain());
        parameters.setCoseStructureType(COSEStructureType.COSE_SIGN1);
        parameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        parameters.setSigDMechanism(SigDMechanism.OBJECT_ID_BY_URI_HASH);
        parameters.setSignatureLevel(SignatureLevel.CB_AdES_BASELINE_LT);

        ToBeSigned dataToSign = service.getDataToSign(digestDocument, parameters);
        SignatureValue signatureValue = getToken().sign(dataToSign, parameters.getDigestAlgorithm(), getPrivateKeyEntry());
        DSSDocument signedDocument = service.signDocument(digestDocument, parameters, signatureValue);

        CBAdESSignatureParameters extensionParameters = new CBAdESSignatureParameters();
        extensionParameters.setSignatureLevel(SignatureLevel.CB_AdES_BASELINE_LTA);
        extensionParameters.setDetachedContents(Collections.singletonList(digestDocument));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.extendDocument(signedDocument, extensionParameters));
        assertEquals("CB-AdES-BASELINE-LTA requires complete binaries of signed documents! " +
                "Extension with a DigestDocument is not possible.", exception.getMessage());
    }

    @Override
    protected String getSigningAlias() {
        return GOOD_USER;
    }

}
