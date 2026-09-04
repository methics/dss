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
package eu.europa.esig.dss.asic.cades.extension.asics.extender;

import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.model.DSSDocument;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ASiCsWithCAdESExtenderBToLTAWithTstParamsTest extends ASiCsWithCAdESExtenderBToLTATest {

    private boolean extended;

    @Override
    protected DSSDocument extendSignature(DSSDocument signedDocument) throws Exception {
        DSSDocument extendedSignature = super.extendSignature(signedDocument);
        extended = true;
        return extendedSignature;
    }

    @Override
    protected ASiCWithCAdESSignatureParameters getExtensionParameters() {
        ASiCWithCAdESSignatureParameters extensionParameters = super.getExtensionParameters();
        extensionParameters.getSignatureTimestampParameters().setDigestAlgorithm(DigestAlgorithm.SHA384);
        extensionParameters.getArchiveTimestampParameters().setDigestAlgorithm(DigestAlgorithm.SHA512);
        return extensionParameters;
    }

    @Override
    protected void checkTimestamps(DiagnosticData diagnosticData) {
        super.checkTimestamps(diagnosticData);

        List<TimestampWrapper> timestampList = diagnosticData.getTimestampList();
        if (extended) {
            assertEquals(2, timestampList.size());

            boolean sigTstFound = false;
            boolean arcTstFound = false;
            for (TimestampWrapper timestampWrapper : timestampList) {
                if (TimestampType.SIGNATURE_TIMESTAMP == timestampWrapper.getType()) {
                    assertEquals(DigestAlgorithm.SHA384, timestampWrapper.getMessageImprint().getDigestMethod());
                    sigTstFound = true;
                } else if (TimestampType.ARCHIVE_TIMESTAMP == timestampWrapper.getType()) {
                    assertEquals(DigestAlgorithm.SHA512, timestampWrapper.getMessageImprint().getDigestMethod());
                    arcTstFound = true;
                }
            }
            assertTrue(sigTstFound);
            assertTrue(arcTstFound);
        } else {
            assertEquals(0, timestampList.size());
        }
    }

}
